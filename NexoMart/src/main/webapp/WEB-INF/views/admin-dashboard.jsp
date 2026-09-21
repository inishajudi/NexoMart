<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jspf" %>

<style>
    .page-title { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 36px; color: #2b2418; margin: 32px 0 20px; }
    .sand-card { background: #fbf6ea; border: 1px solid #e3d5b8; border-radius: 16px; padding: 24px; box-shadow: 0 10px 30px rgba(120,88,30,0.06); margin-bottom: 24px; }
    .sand-card h3 { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 24px; color: #2b2418; margin: 0 0 14px; }
    .table-wrap { overflow-x: auto; }
    .sand-table { width: 100%; border-collapse: collapse; }
    .sand-table th { text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.06em; color: #7a6b52; padding: 10px 12px; border-bottom: 2px solid #e3d5b8; white-space: nowrap; }
    .sand-table td { padding: 12px; border-bottom: 1px solid #eadfc6; color: #2b2418; vertical-align: middle; font-size: 14px; }
    .sand-table tr:last-child td { border-bottom: 0; }
    .sand-table tr:hover td { background: #f6eedb; }
    .sand-table td.price { font-weight: 600; color: #9a6a24; }
    .role-badge, .status-badge { display: inline-block; padding: 3px 12px; font-size: 12px; font-weight: 600; letter-spacing: 0.04em; color: #7f5619; background: #f0e2c0; border-radius: 999px; }
    .sand-table button.btn-danger { padding: 7px 14px; font-size: 13px; font-weight: 600; font-family: inherit; color: #a33a2a; background: transparent; border: 1px solid #d9a99f; border-radius: 8px; cursor: pointer; }
    .sand-table button.btn-danger:hover { background: #f6e3de; }
</style>

<h2 class="page-title">Admin Dashboard</h2>

<div class="sand-card">
    <h3>Users (<c:out value="${users.size()}"/>)</h3>
    <div class="table-wrap">
    <table class="sand-table">
        <tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Created</th></tr>
        <c:forEach var="u" items="${users}">
            <tr>
                <td><c:out value="${u.id}"/></td>
                <td><c:out value="${u.name}"/></td>
                <td><c:out value="${u.email}"/></td>
                <td><span class="role-badge"><c:out value="${u.role}"/></span></td>
                <td><c:out value="${u.createdAt}"/></td>
            </tr>
        </c:forEach>
    </table>
    </div>
</div>

<div class="sand-card">
    <h3>Products (<c:out value="${products.size()}"/>)</h3>
    <div class="table-wrap">
    <table class="sand-table">
        <tr><th>ID</th><th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th>Seller ID</th><th></th></tr>
        <c:forEach var="p" items="${products}">
            <tr>
                <td><c:out value="${p.id}"/></td>
                <td><c:out value="${p.name}"/></td>
                <td><c:out value="${p.category}"/></td>
                <td class="price"><c:out value="${p.price}"/></td>
                <td><c:out value="${p.stockQty}"/></td>
                <td><c:out value="${p.sellerId}"/></td>
                <td>
                    <form method="post" action="<c:url value='/admin/products/remove'/>"
                          onsubmit="return confirm('Remove this listing? This cannot be undone.');">
                        <input type="hidden" name="id" value="${p.id}">
                        <button type="submit" class="btn-danger">Remove</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
    </div>
</div>

<div class="sand-card">
    <h3>Orders (<c:out value="${orders.size()}"/>)</h3>
    <div class="table-wrap">
    <table class="sand-table">
        <tr><th>ID</th><th>Buyer</th><th>Status</th><th>Total</th><th>Placed</th></tr>
        <c:forEach var="o" items="${orders}">
            <tr>
                <td><c:out value="${o.id}"/></td>
                <td><c:out value="${buyerNames[o.buyerId]}"/></td>
                <td><span class="status-badge"><c:out value="${o.status}"/></span></td>
                <td class="price"><c:out value="${o.totalAmount}"/></td>
                <td><c:out value="${o.createdAt}"/></td>
            </tr>
        </c:forEach>
    </table>
    </div>
</div>

<%@ include file="footer.jspf" %>