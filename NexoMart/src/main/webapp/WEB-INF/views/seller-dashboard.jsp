<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<style>
    .page-title  { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 36px; color: #1a1a1a; margin: 32px 0 20px; }
    .stat-row    { display: flex; gap: 20px; margin-bottom: 28px; flex-wrap: wrap; }
    .stat-card   { background: #ffffff; border: 1px solid #e0e0e0; border-radius: 16px; padding: 24px 32px; box-shadow: 0 4px 16px rgba(0,0,0,0.08); flex: 1; min-width: 180px; }
    .stat-label  { font-size: 13px; color: #555; text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 8px; }
    .stat-value  { font-size: 32px; font-weight: 700; color: #1a1a1a; }
    .sand-card   { background: #ffffff; border: 1px solid #e0e0e0; border-radius: 16px; padding: 24px; box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
    .sand-table  { width: 100%; border-collapse: collapse; }
    .sand-table th { text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.06em; color: #555; padding: 10px 12px; border-bottom: 2px solid #e0e0e0; }
    .sand-table td { padding: 12px; border-bottom: 1px solid #eee; color: #1a1a1a; }
    .sand-table tr:last-child td { border-bottom: 0; }
    .empty-note  { color: #555; }
    a.btn-back,
a.btn-back:link,
a.btn-back:visited,
a.btn-back:hover,
a.btn-back:active {
    display: inline-block;
    margin-bottom: 16px;
    padding: 12px 24px;
    background-color: #1a1a1a !important;
    color: #ffffff !important;
    -webkit-text-fill-color: #ffffff !important;
    border: 1px solid #1a1a1a;
    border-radius: 999px;
    font-size: 15px;
    font-weight: 600;
    text-decoration: none !important;
}
</style>

<h2 class="page-title">Sales Dashboard</h2>

<a href="${pageContext.request.contextPath}/orders/seller" class="btn-back">← Back to Orders</a>

<div class="stat-row">
    <div class="stat-card">
        <div class="stat-label">Total Orders</div>
        <div class="stat-value">${dashboard.totalOrders}</div>
    </div>
    <div class="stat-card">
        <div class="stat-label">Total Revenue</div>
        <div class="stat-value">&#8377;<fmt:formatNumber value="${dashboard.totalRevenue}" minFractionDigits="2" maxFractionDigits="2"/></div>
    </div>
</div>

<div class="sand-card">
    <c:choose>
        <c:when test="${empty dashboard.productStats}">
            <p class="empty-note">No sales data yet.</p>
        </c:when>
        <c:otherwise>
            <table class="sand-table">
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Units Sold</th>
                        <th>Revenue</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="stat" items="${dashboard.productStats}">
                        <tr>
                            <td><c:out value="${stat.productName}"/></td>
                            <td>${stat.unitsSold}</td>
                            <td>&#8377;<fmt:formatNumber value="${stat.revenue}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="footer.jspf" %>
