<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>NexoMart - Product</title>
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
    <div id="productDetail"><!-- populated by js/app.js --></div>

    <h2>Reviews</h2>
    <div id="ratingSummary"></div>

    <form id="reviewForm" class="listing-form">
      <p>Only buyers with a <strong>delivered</strong> order for this product can review it.</p>
      <select id="ratingInput" required>
        <option value="">Rating</option>
        <option value="5">5 - Excellent</option>
        <option value="4">4 - Good</option>
        <option value="3">3 - Average</option>
        <option value="2">2 - Poor</option>
        <option value="1">1 - Terrible</option>
      </select>
      <textarea id="reviewCommentInput" placeholder="Share your experience (optional)"></textarea>
      <div class="form-actions">
        <button type="submit">Submit review</button>
      </div>
    </form>
    <div id="reviewFormError" class="form-error"></div>

    <div id="reviewsList"><!-- populated by js/app.js --></div>
  </main>

  <script src="${pageContext.request.contextPath}/js/app.js"></script>
  <script>
    const params = new URLSearchParams(window.location.search);
    NexoMart.initProductDetailPage(Number(params.get('id')));
  </script>
</body>
</html>
