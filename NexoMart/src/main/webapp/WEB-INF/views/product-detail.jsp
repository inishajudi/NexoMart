<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<div class="card">
    <img src="<c:out value='${product.imageUrl}'/>" alt="" style="width:100%; max-height:280px; object-fit:cover; border-radius:6px;" onerror="this.style.display='none'">
    <h2 style="margin:12px 0 4px;"><c:out value="${product.name}"/></h2>
    <div class="muted"><c:out value="${product.category}"/></div>
    <p><c:out value="${product.description}"/></p>
    <div class="price"><fmt:formatNumber value="${product.price}" minFractionDigits="2" maxFractionDigits="2"/></div>
    <div class="muted">
        <c:choose>
            <c:when test="${product.stockQty > 0}">${product.stockQty} in stock</c:when>
            <c:otherwise>Out of stock</c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty averageRating}">
        <div class="muted" style="margin-top:8px;">
            Average rating: <strong><fmt:formatNumber value="${averageRating}" minFractionDigits="1" maxFractionDigits="1"/></strong> / 5
            (<c:out value="${reviews.size()}"/> review<c:if test="${reviews.size() != 1}">s</c:if>)
        </div>
    </c:if>

    <c:if test="${product.stockQty > 0}">
        <form method="post" action="<c:url value='/cart/add'/>" style="margin-top:12px; display:flex; gap:8px;">
            <input type="hidden" name="productId" value="${product.id}">
            <input type="number" name="quantity" value="1" min="1" max="${product.stockQty}" style="width:70px; margin:0;">
            <button type="submit">Add to cart</button>
        </form>
    </c:if>

    <a href="<c:url value='/products'/>" style="display:inline-block; margin-top:12px; color:#9a6a24; font-weight:600; text-decoration:none;">&larr; Back to products</a>
</div>

<c:if test="${sessionScope.loggedInUser != null}">
<div class="card">
    <h3>Write a review</h3>
    <form method="post" action="<c:url value='/reviews/add'/>">
        <input type="hidden" name="productId" value="${product.id}">
        <label>Rating (1-5):
            <input type="number" name="rating" min="1" max="5" required>
        </label>
        <label>Comment:
            <textarea name="comment" maxlength="1000"></textarea>
        </label>
        <button type="submit">Submit Review</button>
    </form>
    <p class="muted">You can only review products from your completed orders, once per product.</p>
</div>
</c:if>

<div class="card">
    <h3>Reviews (<c:out value="${reviews.size()}"/>)</h3>
    <c:choose>
        <c:when test="${empty reviews}">
            <p class="muted">No reviews yet.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="r" items="${reviews}">
                <div class="review-item">
                    <strong><c:out value="${r.reviewerName}"/></strong>
                    &#8212; <c:out value="${r.rating}"/>/5
                    <span class="muted">(<c:out value="${r.createdAt}"/>)</span>
                    <p><c:out value="${r.comment}"/></p>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="footer.jspf" %>