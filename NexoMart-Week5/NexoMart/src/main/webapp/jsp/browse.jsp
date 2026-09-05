<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>NexoMart - Browse</title>
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
      <a href="${pageContext.request.contextPath}/jsp/cart.jsp">Cart (<span id="cartCount">0</span>)</a>
    </nav>
  </header>

  <main>
    <section class="filters">
      <input type="text" id="searchInput" placeholder="Search products...">
      <select id="categorySelect">
        <option value="">All categories</option>
        <option value="Electronics">Electronics</option>
        <option value="Apparel">Apparel</option>
        <option value="Stationery">Stationery</option>
      </select>
      <input type="number" id="minPriceInput" placeholder="Min price" min="0" step="0.01">
      <input type="number" id="maxPriceInput" placeholder="Max price" min="0" step="0.01">
      <select id="sortSelect">
        <option value="NEWEST">Newest</option>
        <option value="PRICE_ASC">Price: low to high</option>
        <option value="PRICE_DESC">Price: high to low</option>
        <option value="NAME_ASC">Name: A-Z</option>
      </select>
      <button id="searchBtn">Search</button>
    </section>

    <section id="productGrid" class="product-grid">
      <!-- Populated by js/app.js via fetch('/api/v1/products') -->
    </section>
  </main>

  <script src="${pageContext.request.contextPath}/js/app.js"></script>
  <script>NexoMart.initBrowsePage();</script>
</body>
</html>
