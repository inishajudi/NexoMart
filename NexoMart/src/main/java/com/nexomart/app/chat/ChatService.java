package com.nexomart.app.chat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guardrails around a ChatProvider:
 *  - input validation (blank / too long)          -> IllegalArgumentException
 *  - per-session rate limit (10 messages/minute)  -> RateLimitExceededException
 *  - per-session cache for repeated questions
 *  - provider failure                             -> static degraded reply (never an exception)
 *
 * The servlet maps IllegalArgumentException to HTTP 400 and
 * RateLimitExceededException to HTTP 429.
 */
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    public static final int MAX_INPUT_CHARS = 500;
    public static final int RATE_LIMIT = 10;
    public static final Duration RATE_WINDOW = Duration.ofMinutes(1);

    static final int MAX_CACHE_PER_SESSION = 20;
    static final int SESSION_CLEANUP_THRESHOLD = 1000;
    static final Duration SESSION_IDLE = Duration.ofMinutes(30);

    public static final String DEGRADED_REPLY =
            "The assistant is temporarily unavailable. Please try again in a moment, "
          + "or browse the Products page in the meantime.";

    private final ChatProvider provider;
    private final Clock clock;
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public ChatService(ChatProvider provider) {
        this(provider, Clock.systemUTC());
    }

    /** Package-private: lets tests control time. */
    ChatService(ChatProvider provider, Clock clock) {
        this.provider = provider;
        this.clock = clock;
    }

    public String answer(String sessionId, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message must not be empty.");
        }
        String text = message.trim();
        if (text.length() > MAX_INPUT_CHARS) {
            throw new IllegalArgumentException(
                    "Message is too long (max " + MAX_INPUT_CHARS + " characters).");
        }

        String sid = (sessionId == null || sessionId.isBlank()) ? "anonymous" : sessionId;
        Instant now = clock.instant();
        evictIdleSessions(now);

        Session session = sessions.computeIfAbsent(sid, k -> new Session());
        String key = normalize(text);

        synchronized (session) {
            session.lastSeen = now;
            checkRateLimit(session, now);
            String cached = session.cache.get(key);
            if (cached != null) {
                return cached;
            }
        }

        // Provider call happens outside the lock so a slow call never blocks other requests.
        try {
            String reply = provider.reply(text);
            if (reply == null || reply.isBlank()) {
                log.warn("Chat provider '{}' returned an empty reply", provider.name());
                return DEGRADED_REPLY;
            }
            synchronized (session) {
                session.cache.put(key, reply);
            }
            return reply;
        } catch (ChatProviderException | RuntimeException e) {
            log.warn("Chat provider '{}' failed: {}", provider.name(), e.toString());
            return DEGRADED_REPLY;
        }
    }

    private void checkRateLimit(Session session, Instant now) {
        Instant windowStart = now.minus(RATE_WINDOW);
        while (!session.hits.isEmpty() && !session.hits.peekFirst().isAfter(windowStart)) {
            session.hits.pollFirst();
        }
        if (session.hits.size() >= RATE_LIMIT) {
            Instant oldest = session.hits.peekFirst();
            long wait = Duration.between(now, oldest.plus(RATE_WINDOW)).getSeconds() + 1;
            throw new RateLimitExceededException(Math.max(1, wait));
        }
        session.hits.addLast(now);
    }

    private void evictIdleSessions(Instant now) {
        if (sessions.size() > SESSION_CLEANUP_THRESHOLD) {
            sessions.values().removeIf(s ->
                    s.lastSeen != null && Duration.between(s.lastSeen, now).compareTo(SESSION_IDLE) > 0);
        }
    }

    private static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private static final class Session {
        final Deque<Instant> hits = new ArrayDeque<>();
        final Map<String, String> cache = new LinkedHashMap<>(16, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > MAX_CACHE_PER_SESSION;
            }
        };
        volatile Instant lastSeen;
    }
}
