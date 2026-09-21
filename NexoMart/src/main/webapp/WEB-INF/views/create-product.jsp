<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jspf" %>

<style>
    .auth-wrap { padding: 40px 0 40px; }
    .auth-card {
        max-width: 520px; margin: 0 auto; padding: 36px 34px;
        background: #fbf6ea; border: 1px solid #e3d5b8; border-radius: 16px;
        box-shadow: 0 10px 30px rgba(120, 88, 30, 0.08);
    }
    .auth-card .eyebrow { display: inline-block; margin-bottom: 10px; }
    .auth-card h2 { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 32px; margin: 0 0 6px; color: #2b2418; }
    .auth-sub { color: #7a6b52; margin: 0 0 20px; font-size: 15px; }
    .auth-card label { display: block; font-size: 13px; font-weight: 600; letter-spacing: 0.02em; color: #5b4d36; margin: 16px 0 6px; }
    .auth-card input, .auth-card textarea, .auth-card select {
        width: 100%; box-sizing: border-box; padding: 12px 14px; font-size: 15px;
        font-family: inherit; color: #2b2418; background: #fffdf7;
        border: 1px solid #dccca8; border-radius: 10px; outline: none;
        transition: border-color .15s, box-shadow .15s;
    }
    .auth-card input:focus, .auth-card textarea:focus { border-color: #9a6a24; box-shadow: 0 0 0 3px rgba(154, 106, 36, 0.18); }
    .auth-card button[type=submit] {
        width: 100%; margin-top: 24px; padding: 13px 16px; font-size: 15px;
        font-weight: 600; font-family: inherit; color: #fff; background: #9a6a24;
        border: 0; border-radius: 10px; cursor: pointer; transition: background .15s;
    }
    .auth-card button[type=submit]:hover { background: #7f5619; }
</style>

<div class="auth-wrap">
    <div class="auth-card">
        <span class="eyebrow">Seller tools</span>
        <h2>List a new product</h2>
        <p class="auth-sub">Add the details below to put your item on the marketplace.</p>

        <form method="post" action="<c:url value='/products/create'/>">
            <label>Name</label>
            <input type="text" name="name" required>

            <label>Description</label>
            <textarea name="description" rows="3"></textarea>

            <label>Category</label>
            <input type="text" name="category" required>

            <label>Price (&#8377;)</label>
            <input type="number" name="price" step="0.01" min="0.01" required>

            <label>Stock quantity</label>
            <input type="number" name="stockQty" min="0" required>

            <label>Image URL (optional)</label>
            <input type="text" name="imageUrl" placeholder="https://...">

            <button type="submit">Create listing</button>
        </form>
    </div>
</div>

<%@ include file="footer.jspf" %>