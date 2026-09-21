package com.nexomart.app.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatServiceTest {

    static class TestClock extends Clock {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        void advance(Duration d) {
            now = now.plus(d);
        }

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    private ChatProvider provider;
    private TestClock clock;
    private ChatService service;

    @BeforeEach
    void setUp() {
        provider = mock(ChatProvider.class);
        clock = new TestClock();
        service = new ChatService(provider, clock);
    }

    @Test
    void returnsProviderReply() throws Exception {
        when(provider.reply("hi")).thenReturn("hello there");
        assertEquals("hello there", service.answer("s1", "hi"));
    }

    @Test
    void identicalQuestionIsCachedPerSession() throws Exception {
        when(provider.reply("hi")).thenReturn("hello there");
        service.answer("s1", "hi");
        service.answer("s1", "hi");
        verify(provider, times(1)).reply("hi");
    }

    @Test
    void cacheIsNotSharedBetweenSessions() throws Exception {
        when(provider.reply("hi")).thenReturn("hello there");
        service.answer("s1", "hi");
        service.answer("s2", "hi");
        verify(provider, times(2)).reply("hi");
    }

    @Test
    void rejectsBlankMessage() {
        assertThrows(IllegalArgumentException.class, () -> service.answer("s1", "   "));
    }

    @Test
    void rejectsTooLongMessage() {
        String tooLong = "a".repeat(ChatService.MAX_INPUT_CHARS + 1);
        assertThrows(IllegalArgumentException.class, () -> service.answer("s1", tooLong));
    }

    @Test
    void acceptsMessageOfExactlyMaxLength() throws Exception {
        String exact = "a".repeat(ChatService.MAX_INPUT_CHARS);
        when(provider.reply(exact)).thenReturn("ok");
        assertEquals("ok", service.answer("s1", exact));
    }

    @Test
    void rateLimitBlocksMessageNumberEleven() throws Exception {
        when(provider.reply(anyString())).thenReturn("ok");
        for (int i = 0; i < ChatService.RATE_LIMIT; i++) {
            service.answer("s1", "question " + i);
        }
        RateLimitExceededException ex = assertThrows(RateLimitExceededException.class,
                () -> service.answer("s1", "one more"));
        assertTrue(ex.getRetryAfterSeconds() > 0);
    }

    @Test
    void rateLimitResetsAfterWindow() throws Exception {
        when(provider.reply(anyString())).thenReturn("ok");
        for (int i = 0; i < ChatService.RATE_LIMIT; i++) {
            service.answer("s1", "question " + i);
        }
        clock.advance(ChatService.RATE_WINDOW.plusSeconds(1));
        assertEquals("ok", service.answer("s1", "after wait"));
    }

    @Test
    void rateLimitIsPerSession() throws Exception {
        when(provider.reply(anyString())).thenReturn("ok");
        for (int i = 0; i < ChatService.RATE_LIMIT; i++) {
            service.answer("s1", "question " + i);
        }
        assertEquals("ok", service.answer("s2", "fresh session"));
    }

    @Test
    void cacheHitsStillCountForRateLimit() throws Exception {
        when(provider.reply("hi")).thenReturn("ok");
        for (int i = 0; i < ChatService.RATE_LIMIT; i++) {
            service.answer("s1", "hi");
        }
        assertThrows(RateLimitExceededException.class, () -> service.answer("s1", "hi"));
    }

    @Test
    void providerFailureGivesDegradedReplyAndIsNotCached() throws Exception {
        when(provider.reply("hi"))
                .thenThrow(new ChatProviderException("boom"))
                .thenReturn("recovered");
        assertEquals(ChatService.DEGRADED_REPLY, service.answer("s1", "hi"));
        assertEquals("recovered", service.answer("s1", "hi"));
    }

    @Test
    void emptyReplyGivesDegradedReply() throws Exception {
        when(provider.reply("hi")).thenReturn("   ");
        assertEquals(ChatService.DEGRADED_REPLY, service.answer("s1", "hi"));
    }

    @Test
    void runtimeExceptionGivesDegradedReply() throws Exception {
        when(provider.reply("hi")).thenThrow(new IllegalStateException("bug"));
        assertEquals(ChatService.DEGRADED_REPLY, service.answer("s1", "hi"));
    }
}