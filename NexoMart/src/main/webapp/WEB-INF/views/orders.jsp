<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<style>
    .page-title { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 36px; color: #2b2418; margin: 32px 0 20px; }
    .sand-card { background: #fbf6ea; border: 1px solid #e3d5b8; border-radius: 16px; padding: 24px; box-shadow: 0 10px 30px rgba(120,88,30,0.06); margin-bottom: 20px; }
    .order-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; color: #2b2418; }
    .order-date { color: #7a6b52; font-size: 14px; }
    .status-badge { display: inline-block; padding: 3px 12px; font-size: 12px; font-weight: 600; letter-spacing: 0.04em; color: #7f5619; background: #f0e2c0; border-radius: 999px; }
    .table-wrap { overflow-x: auto; }
    .sand-table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    .sand-table th { text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.06em; color: #7a6b52; padding: 10px 12px; border-bottom: 2px solid #e3d5b8; }
    .sand-table td { padding: 12px; border-bottom: 1px solid #eadfc6; color: #2b2418; }
    .sand-table tr:last-child td { border-bottom: 0; }
    .total-line { text-align: right; font-size: 18px; margin: 14px 0 0; color: #2b2418; }
    .total-line strong { color: #9a6a24; }
    .empty-note { color: #7a6b52; margin: 0; }
</style>

<h2 class="page-title">Your orders</h2>

<c:choose>
<c:when test="${empty orders}">
    <div class="sand-card"><p class="empty-note">You haven't placed any orders yet.</p></div>
</c:when>
<c:otherwise>
    <c:forEach var="order" items="${orders}">
        <div class="sand-card">
            <div class="order-head">
                <strong>Order #${order.id}</strong>
                <span class="order-date"><c:out value="${order.createdAt}"/></span>
            </div>
            <div>Status: <span class="status-badge"><c:out value="${order.status}"/></span></div>
            <div class="table-wrap">
            <table class="sand-table">
                <thead><tr><th>Product</th><th>Qty</th><th>Unit price</th><th>Line total</th></tr></thead>
                <tbody>
                <c:forEach var="item" items="${order.items}">
                    <tr>
                        <td><c:out value="${item.productName}"/></td>
                        <td>${item.quantity}</td>
                        <td>&#8377;<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>&#8377;<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
            <div class="total-line">Total: <strong>&#8377;<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></strong></div>
        </div>
    </c:forEach>
</c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>