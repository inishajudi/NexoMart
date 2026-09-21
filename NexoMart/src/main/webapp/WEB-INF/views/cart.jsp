<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<style>
    .page-title { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 36px; color: #2b2418; margin: 32px 0 20px; }
    .sand-card { background: #fbf6ea; border: 1px solid #e3d5b8; border-radius: 16px; padding: 24px; box-shadow: 0 10px 30px rgba(120,88,30,0.06); margin-bottom: 20px; }
    .table-wrap { overflow-x: auto; }
    .sand-table { width: 100%; border-collapse: collapse; }
    .sand-table th { text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.06em; color: #7a6b52; padding: 10px 12px; border-bottom: 2px solid #e3d5b8; }
    .sand-table td { padding: 14px 12px; border-bottom: 1px solid #eadfc6; color: #2b2418; vertical-align: middle; }
    .sand-table tr:last-child td { border-bottom: 0; }
    .sand-table input[type=number] { width: 70px; margin: 0; padding: 8px 10px; font-family: inherit; color: #2b2418; background: #fffdf7; border: 1px solid #dccca8; border-radius: 8px; }
    .sand-table button, .sand-btn { padding: 9px 16px; font-size: 14px; font-weight: 600; font-family: inherit; color: #fff; background: #9a6a24; border: 0; border-radius: 8px; cursor: pointer; }
    .sand-table button:hover, .sand-btn:hover { background: #7f5619; }
    .sand-table button.btn-danger { background: transparent; color: #a33a2a; border: 1px solid #d9a99f; }
    .sand-table button.btn-danger:hover { background: #f6e3de; }
    .total-line { text-align: right; font-size: 22px; margin: 20px 0 14px; color: #2b2418; }
    .total-line strong { color: #9a6a24; }
    .empty-note { color: #7a6b52; margin: 0; }
    .empty-note a { color: #9a6a24; font-weight: 600; text-decoration: none; }
</style>

<h2 class="page-title">Your cart</h2>

<c:choose>
<c:when test="${empty cartItems}">
    <div class="sand-card"><p class="empty-note">Your cart is empty. <a href="<c:url value='/products'/>">Browse products</a></p></div>
</c:when>
<c:otherwise>
<div class="sand-card">
    <div class="table-wrap">
    <table class="sand-table">
        <thead>
            <tr><th>Product</th><th>Unit price</th><th>Quantity</th><th>Line total</th><th></th></tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${cartItems}">
            <tr>
                <td><c:out value="${item.productName}"/></td>
                <td>&#8377;<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td>
                    <form method="post" action="<c:url value='/cart/update'/>" style="display:flex; gap:6px;">
                        <input type="hidden" name="cartItemId" value="${item.id}">
                        <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.availableStock}">
                        <button type="submit">Update</button>
                    </form>
                </td>
                <td>&#8377;<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td>
                    <form method="post" action="<c:url value='/cart/remove'/>">
                        <input type="hidden" name="cartItemId" value="${item.id}">
                        <button type="submit" class="btn-danger">Remove</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>
    <div class="total-line">Total: <strong>&#8377;<fmt:formatNumber value="${cartTotal}" minFractionDigits="2" maxFractionDigits="2"/></strong></div>
    <div style="text-align:right;">
        <a class="btn" href="<c:url value='/checkout'/>">Proceed to checkout</a>
    </div>
</div>
</c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>