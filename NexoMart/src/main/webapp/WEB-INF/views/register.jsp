<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jspf" %>

<style>
    .auth-wrap { padding: 48px 0 40px; }
    .auth-card {
        max-width: 440px; margin: 0 auto; padding: 36px 34px;
        background: var(--surface); border: 1px solid var(--border);
        border-radius: 20px; box-shadow: var(--shadow-lg);
    }
    .auth-card h2 {
        font-size: 32px; font-weight: 800; letter-spacing: -0.04em;
        margin: 0 0 6px; color: var(--text);
    }
    .auth-sub { color: var(--text-muted); margin: 0 0 24px; font-size: 15px; }
    .auth-card label {
        display: block; font-size: 12px; font-weight: 700;
        letter-spacing: 0.06em; text-transform: uppercase;
        color: var(--text-muted); margin: 16px 0 5px;
    }
    .auth-card input, .auth-card select {
        background: var(--surface-2); color: var(--text);
        border: 1px solid var(--border-strong); border-radius: var(--radius-sm);
        padding: 12px 14px; font-size: 15px; width: 100%;
        box-sizing: border-box; font-family: inherit; outline: none;
        transition: border-color .15s, box-shadow .15s; margin: 0;
    }
    .auth-card input:focus, .auth-card select:focus {
        border-color: var(--accent);
        box-shadow: 0 0 0 3px rgba(167,139,250,0.2);
    }
    .auth-card input::placeholder { color: var(--text-faint); }
    .auth-card select option { background: var(--surface-2); color: var(--text); }
    .auth-card button[type=submit] {
        width: 100%; margin-top: 24px; padding: 13px 16px; font-size: 15px;
        font-weight: 700; font-family: inherit; color: #fff;
        background: var(--accent);color:#111; border: 0; border-radius: 999px;
        cursor: pointer; transition: background .15s, box-shadow .15s;
        box-shadow: 0 4px 16px rgba(255,255,255,0.10);
    }
    .auth-card button[type=submit]:hover {
        background: var(--accent-hover);
        box-shadow: 0 6px 20px rgba(255,255,255,0.15);
    }
    .auth-foot { margin: 22px 0 0; text-align: center; font-size: 14px; color: var(--text-muted); }
    .auth-foot a { color: var(--accent); font-weight: 600; text-decoration: none; }
    .auth-foot a:hover { text-decoration: underline; }
</style>

<div class="auth-wrap">
    <div class="auth-card">
        <span class="eyebrow">Join NexoMart</span>
        <h2>Create an account</h2>
        <p class="auth-sub">Buy from chosen sellers, or start selling your own goods.</p>

        <form method="post" action="<c:url value='/register'/>">
            <label>Name</label>
            <input type="text" name="name" value="<c:out value='${formName}'/>" placeholder="Your full name" required>

            <label>Email</label>
            <input type="email" name="email" value="<c:out value='${formEmail}'/>" placeholder="you@example.com" required>

            <label>Password (min 8 characters)</label>
            <input type="password" name="password" placeholder="••••••••" minlength="8" required>

            <label>I am a...</label>
            <select name="role" required>
                <option value="BUYER" <c:if test="${formRole == 'BUYER'}">selected</c:if>>Buyer</option>
                <option value="SELLER" <c:if test="${formRole == 'SELLER'}">selected</c:if>>Seller</option>
            </select>

            <button type="submit">Create account</button>
        </form>

        <p class="auth-foot">Already have an account? <a href="<c:url value='/login'/>">Log in</a></p>
    </div>
</div>

<%@ include file="footer.jspf" %>