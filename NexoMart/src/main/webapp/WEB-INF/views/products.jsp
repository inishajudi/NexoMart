<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<style>
    /* Search bar */
    .search-bar {
        display: flex; gap: 12px; align-items: flex-end;
        background: var(--surface); border: 1px solid var(--border);
        border-radius: var(--radius); padding: 18px 20px; margin-bottom: 28px;
    }
    .search-bar > div { flex: 1; }
    .search-bar input { margin: 4px 0 0; }
    .search-bar button { flex-shrink: 0; margin-bottom: 0; align-self: flex-end; }

    /* Out of stock badge */
    .oos-badge {
        display: inline-block; font-size: 11px; font-weight: 700;
        background: var(--error-bg); color: var(--error-text);
        border: 1px solid var(--error-border); border-radius: 999px;
        padding: 2px 9px; margin-top: 4px;
    }
    .in-stock { color: #4ade80; font-size: 12px; font-weight: 600; }

    /* Add to cart inline */
    .cart-form { display: flex; gap: 8px; margin-top: 10px; align-items: center; }
    .cart-form input[type=number] {
        width: 62px; margin: 0; padding: 8px 10px; text-align: center;
    }
    .cart-form button { padding: 8px 16px; font-size: 13px; border-radius: 999px; box-shadow: none; }

    /* Seller actions */
    .seller-actions { display: flex; gap: 8px; margin-top: 10px; }
    .seller-actions a button, .seller-actions form button {
        padding: 7px 14px; font-size: 12px; border-radius: 999px; box-shadow: none;
    }
    .seller-actions form button { background: var(--error-bg); color: var(--error-text); border: 1px solid var(--error-border); box-shadow: none; }
    .seller-actions form button:hover { background: #2a0a0a; }

    /* No image placeholder */
    .no-img {
        width: 100%; height: 100%;
        display: flex; align-items: center; justify-content: center;
        color: var(--text-faint); font-size: 13px;
        background: var(--surface-2);
    }
.product-info {
    padding: 16px;
}
.product-img-wrap {
    width: 100%;
    height: 280px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    background: #fff;
    padding: 12px;
    box-sizing: border-box;
}

.product-img-wrap img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    display: block;
}

.product-info {
    padding: 18px 20px 20px;
}

.product-info > * {
    margin-bottom: 8px;
}

.product-info > *:last-child {
    margin-bottom: 0;
}

.product-category {
    margin-bottom: 8px;
    font-size: 12px;
}

.product-name {
    margin-bottom: 12px;
    font-size: 16px;
    font-weight: 700;
    line-height: 1.4;
}

.product-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 10px;
    margin-top: 8px;
}
</style>

<div class="search-bar">
    <form method="get" action="<c:url value='/products'/>" style="display:flex; gap:12px; width:100%; align-items:flex-end;">
        <div style="flex:2;">
            <label>Search</label>
            <input type="text" name="keyword" placeholder="Search by name or description" value="<c:out value='${keyword}'/>">
        </div>
        <div style="flex:1;">
            <label>Category</label>
            <input type="text" name="category" placeholder="e.g. Electronics" value="<c:out value='${category}'/>">
        </div>
        <button type="submit" style="margin-bottom:14px;">Search</button>
    </form>
</div>

<div class="product-grid">
    <c:forEach var="p" items="${products}">
        <div class="card">
            <!-- Image -->
            <a href="<c:url value='/products/view'><c:param name='id' value='${p.id}'/></c:url>" style="text-decoration:none;">
                <div class="product-img-wrap">
                    <c:choose>
                        <c:when test="${not empty p.imageUrl}">
                            <img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>" loading="lazy">
                        </c:when>
                        <c:otherwise>
                            <div class="no-img">No image</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </a>

            <!-- Info -->
            <div class="product-info">
                <div class="product-category"><c:out value="${p.category}"/></div>
                <a href="<c:url value='/products/view'><c:param name='id' value='${p.id}'/></c:url>" style="text-decoration:none;">
                    <div class="product-name"><c:out value="${p.name}"/></div>
                </a>

                <div class="product-footer">
                    <span class="price"><fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2"/></span>
                    <c:choose>
                        <c:when test="${p.stockQty > 0}">
                            <span class="in-stock">&#10003; In stock</span>
                        </c:when>
                        <c:otherwise>
                            <span class="oos-badge">Out of stock</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Add to cart -->
                <c:if test="${p.stockQty > 0}">
                    <form method="post" action="<c:url value='/cart/add'/>" class="cart-form">
                        <input type="hidden" name="productId" value="${p.id}">
                        <input type="number" name="quantity" value="1" min="1" max="${p.stockQty}">
                        <button type="submit">Add to cart</button>
                    </form>
                </c:if>

                <!-- Seller edit/delete -->
                <c:if test="${sessionScope.loggedInUser != null && sessionScope.loggedInUser.id == p.sellerId}">
                    <div class="seller-actions">
                        <a href="<c:url value='/products/edit'><c:param name='id' value='${p.id}'/></c:url>">
                            <button type="button">Edit</button>
                        </a>
                        <form method="post" action="<c:url value='/products/delete'/>"
                              onsubmit="return confirm('Delete this listing?');">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit">Delete</button>
                        </form>
                    </div>
                </c:if>
            </div>
        </div>
    </c:forEach>

    <c:if test="${empty products}">
        <p class="muted" style="grid-column:1/-1; padding:32px 0;">No products found.</p>
    </c:if>
</div>

<%@ include file="footer.jspf" %>
