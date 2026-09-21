<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    .auth-card input, .auth-card textarea {
        width: 100%; box-sizing: border-box; padding: 12px 14px; font-size: 15px;
        font-family: inherit; color: #2b2418; background: #fffdf7;
        border: 1px solid #dccca8; border-radius: 10px; outline: none;
        transition: border-color .15s, box-shadow .15s;
    }
    .auth-card textarea { min-height: 90px; }
    .auth-card input:focus, .auth-card textarea:focus { border-color: #9a6a24; box-shadow: 0 0 0 3px rgba(154, 106, 36, 0.18); }
    .auth-card button[type=submit] {
        width: 100%; margin-top: 24px; padding: 13px 16px; font-size: 15px;
        font-weight: 600; font-family: inherit; color: #fff; background: #9a6a24;
        border: 0; border-radius: 10px; cursor: pointer; transition: background .15s;
    }
    .auth-card button[type=submit]:hover { background: #7f5619; }
    .auth-card button.btn-danger { background: transparent; color: #a33a2a; border: 1px solid #d9a99f; margin-top: 12px; }
    .auth-card button.btn-danger:hover { background: #f6e3de; }
    .form-error { padding: 12px 14px; margin: 0 0 16px; font-size: 14px; color: #8a2f20; background: #f8e6e1; border: 1px solid #e3b8ae; border-radius: 10px; }
    .back-link { display: block; text-align: center; margin-top: 20px; color: #9a6a24; font-weight: 600; text-decoration: none; font-size: 14px; }
    .back-link:hover { text-decoration: underline; }
</style>

<div class="auth-wrap">
    <div class="auth-card">
        <span class="eyebrow">Seller tools</span>
        <h2>Edit listing</h2>
        <p class="auth-sub">Update the details or remove this product.</p>

        <c:if test="${not empty errorMessage}">
            <div class="form-error"><c:out value="${errorMessage}" /></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/products/edit?id=${product.id}" method="post">
            <label>Name</label>
            <input type="text" name="name" value="${fn:escapeXml(product.name)}" required />

            <label>Description</label>
            <textarea name="description">${fn:escapeXml(product.description)}</textarea>

            <label>Price (&#8377;)</label>
            <input type="number" step="0.01" name="price" value="${product.price}" required />

            <label>Stock quantity</label>
            <input type="number" name="stockQty" value="${product.stockQty}" required />

            <label>Category</label>
            <input type="text" name="category" value="${fn:escapeXml(product.category)}" required />

            <label>Image URL</label>
            <input type="text" name="imageUrl" value="${fn:escapeXml(product.imageUrl)}" />

            <button type="submit">Save changes</button>
        </form>

        <form action="${pageContext.request.contextPath}/products/delete" method="post"
              onsubmit="return confirm('Are you sure you want to delete this listing?');">
            <input type="hidden" name="id" value="${product.id}" />
            <button type="submit" class="btn-danger">Delete listing</button>
        </form>

        <a class="back-link" href="${pageContext.request.contextPath}/products">&larr; Back to products</a>
    </div>
</div>

<%@ include file="footer.jspf" %>