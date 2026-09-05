<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>NexoMart - Cart</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <header>
    <h1>NexoMart</h1>
    <nav>
      <a href="${pageContext.request.contextPath}/jsp/browse.jsp">Browse</a>
      <a href="${pageContext.request.contextPath}/jsp/seller-dashboard.jsp">Seller Dashboard</a>
      <a href="${pageContext.request.contextPath}/jsp/admin-dashboard.jsp">Admin Panel</a>
      <a href="${pageContext.request.contextPath}/jsp/my-orders.jsp">My Orders</a>
      <a href="${pageContext.request.contextPath}/jsp/cart.jsp">Cart</a>
    </nav>
  </header>

  <main>
    <h2>Your Cart</h2>
    <table id="cartTable">
      <thead>
        <tr><th>Product</th><th>Price</th><th>Qty</th><th>Line total</th><th></th></tr>
      </thead>
      <tbody id="cartBody"><!-- populated by js/app.js --></tbody>
    </table>

    <div class="cart-summary">
      <strong>Total: ₹<span id="cartTotal">0.00</span></strong>
      <button id="checkoutBtn">Confirm mock payment &amp; place order</button>
    </div>

    <div id="checkoutResult"></div>
  </main>

  <script src="${pageContext.request.contextPath}/js/app.js"></script>
  <script>NexoMart.initCartPage();</script>
</body>
</html>
