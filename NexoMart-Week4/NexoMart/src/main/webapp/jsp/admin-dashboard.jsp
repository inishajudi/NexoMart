<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>NexoMart - Admin Panel</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <header>
    <h1>NexoMart</h1>
    <nav>
      <a href="${pageContext.request.contextPath}/jsp/browse.jsp">Browse</a>
      <a href="${pageContext.request.contextPath}/jsp/seller-dashboard.jsp">Seller Dashboard</a>
      <a href="${pageContext.request.contextPath}/jsp/admin-dashboard.jsp">Admin Panel</a>
      <a href="${pageContext.request.contextPath}/jsp/cart.jsp">Cart</a>
    </nav>
  </header>

  <main>
    <p class="admin-note">Requires an ADMIN-role session. Non-admins will see 403s from the API calls below.</p>

    <h2>Users</h2>
    <table id="usersTable">
      <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Joined</th></tr></thead>
      <tbody id="usersBody"><!-- populated by js/app.js --></tbody>
    </table>

    <h2>All Orders</h2>
    <table id="allOrdersTable">
      <thead><tr><th>Order #</th><th>Status</th><th>Total</th><th>Placed</th></tr></thead>
      <tbody id="allOrdersBody"><!-- populated by js/app.js --></tbody>
    </table>

    <h2>Moderate Listings</h2>
    <div class="filters">
      <input type="text" id="moderateSearchInput" placeholder="Search listings to moderate...">
      <button id="moderateSearchBtn">Search</button>
    </div>
    <table id="moderateTable">
      <thead><tr><th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th></th></tr></thead>
      <tbody id="moderateBody"><!-- populated by js/app.js --></tbody>
    </table>
  </main>

  <script src="${pageContext.request.contextPath}/js/app.js"></script>
  <script>NexoMart.initAdminDashboard();</script>
</body>
</html>
