<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<style>
    
    .page-title { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 36px; color: #1a1a1a; margin: 32px 0 20px; }
    .sand-card { background: #ffffff; border: 1px solid #e0e0e0; border-radius: 16px; padding: 24px; box-shadow: 0 4px 16px rgba(0,0,0,0.08); margin-bottom: 20px; }
    .order-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; color: #1a1a1a; }
    .order-date { color: #555555; font-size: 14px; }
    .order-meta { color: #333333; font-size: 14px; margin-bottom: 6px; }
    .status-badge { display: inline-block; padding: 3px 12px; font-size: 12px; font-weight: 600; letter-spacing: 0.04em; color: #ffffff; background: #888888; border-radius: 999px; }
    .table-wrap { overflow-x: auto; }
    .sand-table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    .sand-table th { text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.06em; color: #555555; padding: 10px 12px; border-bottom: 2px solid #e0e0e0; }
    .sand-table td { padding: 12px; border-bottom: 1px solid #eeeeee; color: #1a1a1a; }
    .sand-table tr:last-child td { border-bottom: 0; }
    .empty-note { color: #555555; margin: 0; }
    .btn-ship { background:#2e7d32; color:#fff; border:none; padding:5px 14px;
                border-radius:999px; font-size:12px; font-weight:600; cursor:pointer; }
    .btn-ship:hover { background:#1b5e20; }
a.sales-dashboard-button,
a.sales-dashboard-button:link,
a.sales-dashboard-button:visited,
a.sales-dashboard-button:hover,
a.sales-dashboard-button:active {
    display: inline-block;
    padding: 12px 24px;
    margin-bottom: 20px;
    background: #1a1a1a !important;
    color: #ffffff !important;
    -webkit-text-fill-color: #ffffff !important;
    border: 1px solid #1a1a1a;
    border-radius: 999px;
    font-size: 16px;
    font-weight: 600;
    text-decoration: none !important;
    box-sizing: border-box;
}
</style>

<h2 class="page-title">Orders for your products</h2>
<a href="${pageContext.request.contextPath}/seller/dashboard"
   class="sales-dashboard-button">
    View Sales Dashboard
</a>
<c:choose>
<c:when test="${empty orders}">
    <div class="sand-card"><p class="empty-note">No orders yet for your products.</p></div>
</c:when>
<c:otherwise>
    <c:forEach var="order" items="${orders}">
        <div class="sand-card">
            <div class="order-head">
                <strong>Order #${order.id}</strong>
                <span class="order-date"><c:out value="${order.createdAt}"/></span>
            </div>
            <div class="order-meta">Buyer ID: <c:out value="${order.buyerId}"/></div>
            <div style="display:flex; align-items:center; gap:12px;">
    Status: <span class="status-badge"><c:out value="${order.status}"/></span>
   
<c:if test="${order.status == 'CONFIRMED'}">
    <form method="post" action="${pageContext.request.contextPath}/orders/seller"
          style="margin:0;">
        <input type="hidden" name="orderId" value="${order.id}"/>
        <input type="hidden" name="action" value="ship"/>
        <button type="submit" class="btn-ship">Mark as Shipped</button>
    </form>
</c:if>
<c:if test="${order.status == 'SHIPPED'}">
    <form method="post" action="${pageContext.request.contextPath}/orders/seller"
          style="margin:0;">
        <input type="hidden" name="orderId" value="${order.id}"/>
        <input type="hidden" name="action" value="deliver"/>
        <button type="submit" class="btn-ship" style="background:#1565c0;">Mark as Delivered</button>
    </form>
</c:if>
</div>
            <div class="table-wrap">
            <table class="sand-table">
                <thead><tr><th>Your product</th><th>Qty</th><th>Unit price</th><th>Line total</th></tr></thead>
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
        </div>
    </c:forEach>
</c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>
