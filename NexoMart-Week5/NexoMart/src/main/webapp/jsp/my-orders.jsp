<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>NexoMart - My Orders</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <header>
    <h1>NexoMart</h1>
    <nav>
      <a href="${pageContext.request.contextPath}/jsp/browse.jsp">Browse</a>
      <a href="${pageContext.request.contextPath}/jsp/cart.jsp">Cart</a>
      <a href="${pageContext.request.contextPath}/jsp/my-orders.jsp">My Orders</a>
      <a href="${pageContext.request.contextPath}/jsp/seller-dashboard.jsp">Seller Dashboard</a>
      <a href="${pageContext.request.contextPath}/jsp/admin-dashboard.jsp">Admin Panel</a>
    </nav>
  </header>

  <main>
    <h2>My Orders</h2>
    <table id="myOrdersTable">
      <thead>
        <tr><th>Order #</th><th>Status</th><th>Total</th><th>Placed</th><th></th></tr>
      </thead>
      <tbody id="myOrdersBody"><!-- populated by js/app.js --></tbody>
    </table>
  </main>

  <script src="${pageContext.request.contextPath}/js/app.js"></script>
  <script>NexoMart.initMyOrdersPage();</script>
</body>
</html>
