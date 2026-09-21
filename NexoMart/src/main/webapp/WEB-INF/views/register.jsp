<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jspf" %>

<style>
    .auth-wrap { padding: 56px 0 40px; }
    .auth-card {
        max-width: 440px; margin: 0 auto; padding: 36px 34px;
        background: #fbf6ea; border: 1px solid #e3d5b8; border-radius: 16px;
        box-shadow: 0 10px 30px rgba(120, 88, 30, 0.08);
    }
    .auth-card .eyebrow { display: inline-block; margin-bottom: 10px; }
    .auth-card h2 {
        font-family: 'DM Serif Display', Georgia, serif; font-weight: 400;
        font-size: 34px; margin: 0 0 6px; color: #2b2418;
    }
    .auth-sub { color: #7a6b52; margin: 0 0 24px; font-size: 15px; }
    .auth-card label {
        display: block; font-size: 13px; font-weight: 600; letter-spacing: 0.02em;
        color: #5b4d36; margin: 16px 0 6px;
    }
    .auth-card input, .auth-card select {
        width: 100%; box-sizing: border-box; padding: 12px 14px; font-size: 15px;
        font-family: inherit; color: #2b2418; background: #fffdf7;
        border: 1px solid #dccca8; border-radius: 10px; outline: none;
        transition: border-color .15s, box-shadow .15s;
    }
    .auth-card input:focus, .auth-card select:focus {
        border-color: #9a6a24; box-shadow: 0 0 0 3px rgba(154, 106, 36, 0.18);
    }
    .auth-card button[type=submit] {
        width: 100%; margin-top: 24px; padding: 13px 16px; font-size: 15px;
        font-weight: 600; font-family: inherit; color: #fff; background: #9a6a24;
        border: 0; border-radius: 10px; cursor: pointer; transition: background .15s;
    }
    .auth-card button[type=submit]:hover { background: #7f5619; }
    .auth-foot { margin: 22px 0 0; text-align: center; font-size: 14px; color: #7a6b52; }
    .auth-foot a { color: #9a6a24; font-weight: 600; text-decoration: none; }
    .auth-foot a:hover { text-decoration: underline; }
</style>

<div class="auth-wrap">
    <div class="auth-card">
        <span class="eyebrow">Join NexoMart</span>
        <h2>Create an account</h2>
        <p class="auth-sub">Buy from chosen sellers, or start selling your own goods.</p>

        <form method="post" action="<c:url value='/register'/>">
            <label>Name</label>
            <input type="text" name="name" value="<c:out value='${formName}'/>" required>

            <label>Email</label>
            <input type="email" name="email" value="<c:out value='${formEmail}'/>" required>

            <label>Password (min 8 characters)</label>
            <input type="password" name="password" minlength="8" required>

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