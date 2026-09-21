<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<style>
    .page-title { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 36px; color: #2b2418; margin: 32px 0 20px; }
    .sand-card { background: #fbf6ea; border: 1px solid #e3d5b8; border-radius: 16px; padding: 24px; box-shadow: 0 10px 30px rgba(120,88,30,0.06); margin-bottom: 20px; }
    .table-wrap { overflow-x: auto; }
    .sand-table { width: 100%; border-collapse: collapse; }
    .sand-table th { text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.06em; color: #7a6b52; padding: 10px 12px; border-bottom: 2px solid #e3d5b8; }
    .sand-table td { padding: 14px 12px; border-bottom: 1px solid #eadfc6; color: #2b2418; }
    .sand-table tr:last-child td { border-bottom: 0; }
    .sand-btn { padding: 12px 22px; font-size: 15px; font-weight: 600; font-family: inherit; color: #fff; background: #9a6a24; border: 0; border-radius: 10px; cursor: pointer; }
    .sand-btn:hover { background: #7f5619; }
    .total-line { text-align: right; font-size: 22px; margin: 20px 0 14px; color: #2b2418; }
    .total-line strong { color: #9a6a24; }
    .mock-note { padding: 12px 14px; margin: 0 0 18px; font-size: 14px; line-height: 1.6; color: #6b5a3e; background: #f3ead8; border: 1px dashed #d9c79e; border-radius: 10px; }
    .empty-note { color: #7a6b52; margin: 0; }
    .empty-note a { color: #9a6a24; font-weight: 600; text-decoration: none; }
</style>

<h2 class="page-title">Checkout</h2>

<c:choose>
<c:when test="${empty cartItems}">
    <div class="sand-card"><p class="empty-note">Your cart is empty. <a href="<c:url value='/products'/>">Browse products</a></p></div>
</c:when>
<c:otherwise>
<div class="sand-card">
    <div class="table-wrap">
    <table class="sand-table">
        <thead><tr><th>Product</th><th>Qty</th><th>Line total</th></tr></thead>
        <tbody>
        <c:forEach var="item" items="${cartItems}">
            <tr>
                <td><c:out value="${item.productName}"/></td>
                <td>${item.quantity}</td>
                <td>&#8377;<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    <div class="total-line">Total: <strong>&#8377;<fmt:formatNumber value="${cartTotal}" minFractionDigits="2" maxFractionDigits="2"/></strong></div>

    <p class="mock-note">This is a mock payment step for the MVP - no real payment gateway is involved. Click below to simulate a successful payment and place your order.</p>

    <form method="post" action="<c:url value='/checkout'/>" style="text-align:right;">
        <button type="submit" class="sand-btn">Confirm Payment</button>
    </form>
</div>
</c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>