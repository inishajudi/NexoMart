<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>NexoMart - Seller Dashboard</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <header>
    <h1>NexoMart</h1>
    <nav>
      <a href="${pageContext.request.contextPath}/jsp/browse.jsp">Browse</a>
      <a href="${pageContext.request.contextPath}/jsp/admin-dashboard.jsp">Admin Panel</a>
      <a href="${pageContext.request.contextPath}/jsp/my-orders.jsp">My Orders</a>
      <a href="${pageContext.request.contextPath}/jsp/cart.jsp">Cart</a>
      <a href="${pageContext.request.contextPath}/jsp/seller-dashboard.jsp">Seller Dashboard</a>
    </nav>
  </header>

  <main>
    <h2>My Listings</h2>

    <form id="listingForm" class="listing-form">
      <input type="hidden" id="productId" value="">
      <input type="text" id="nameInput" placeholder="Product name" required>
      <textarea id="descriptionInput" placeholder="Description"></textarea>
      <input type="number" id="priceInput" placeholder="Price" step="0.01" min="0.01" required>
      <input type="number" id="stockInput" placeholder="Stock quantity" min="0" required>
      <input type="text" id="categoryInput" placeholder="Category">
      <input type="text" id="imageUrlInput" placeholder="Image URL">
      <div class="form-actions">
        <button type="submit" id="saveBtn">Add listing</button>
        <button type="button" id="cancelEditBtn" style="display:none">Cancel edit</button>
      </div>
    </form>
    <div id="formError" class="form-error"></div>

    <table id="listingsTable">
      <thead>
        <tr><th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th></th></tr>
      </thead>
      <tbody id="listingsBody"><!-- populated by js/app.js --></tbody>
    </table>

    <h2>Incoming Orders</h2>
    <table id="incomingOrdersTable">
      <thead>
        <tr><th>Order #</th><th>Status</th><th>Items (yours)</th><th>Placed</th><th>Action</th></tr>
      </thead>
      <tbody id="incomingOrdersBody"><!-- populated by js/app.js --></tbody>
    </table>
  </main>

  <script src="${pageContext.request.contextPath}/js/app.js"></script>
  <script>NexoMart.initSellerDashboard();</script>
</body>
</html>
