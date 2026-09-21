<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/views/header.jspf" %>

<section style="padding:64px 0 44px;">
    <span class="eyebrow">Curated marketplace</span>
    <h1 class="hero-title">Crafted goods.<br><span class="accent">Chosen sellers.</span></h1>
    <p class="hero-subtitle">
        NexoMart brings together independent sellers and buyers who care about
        quality &mdash; every listing backed by a real maker, not a warehouse.
    </p>
    <div class="hero-actions">
        <a href="<c:url value='/products'/>" class="btn">Browse the marketplace</a>
        <a href="<c:url value='/register'/>" class="btn-outline">Start selling
            <svg class="icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
        </a>
    </div>
</section>

<div class="section-heading">
    <h2>Browse by category</h2>
</div>
<div class="category-grid">
    <a href="<c:url value='/products?category=Electronics'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="2" width="16" height="20" rx="2"></rect><line x1="12" y1="18" x2="12.01" y2="18"></line></svg>
        Electronics
    </a>
    <a href="<c:url value='/products?category=Home %26 Kitchen'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
        Home &amp; Kitchen
    </a>
    <a href="<c:url value='/products?category=Baby%20Toys'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2"></path><line x1="9" y1="9" x2="9.01" y2="9"></line><line x1="15" y1="9" x2="15.01" y2="9"></line></svg>
        Baby Toys
    </a>
    <a href="<c:url value='/products?category=Makeup'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
        Makeup
    </a>
    <a href="<c:url value='/products'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
        View all
    </a>
</div>

<div class="section-heading">
    <h2>All products</h2>
    <a href="<c:url value='/products'/>">Browse everything</a>
</div>
<div class="product-grid">
    <c:forEach var="product" items="${featuredProducts}">
        <a href="<c:url value='/products/view'><c:param name='id' value='${product.id}'/></c:url>" class="card" style="color:inherit; text-decoration:none; display:block;">
            <div style="height:150px; background:var(--surface-hover); border-radius:var(--radius-sm); margin-bottom:14px; display:flex; align-items:center; justify-content:center; overflow:hidden;">
                <c:choose>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" style="width:100%; height:100%; object-fit:cover;">
                    </c:when>
                    <c:otherwise>
                        <span class="muted">No image</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="muted" style="text-transform:uppercase; font-size:11px; letter-spacing:0.06em; margin-bottom:6px;"><c:out value="${product.category}"/></div>
            <div style="font-weight:600; margin-bottom:8px;"><c:out value="${product.name}"/></div>
            <div class="price"><c:out value="${product.price}"/></div>
        </a>
    </c:forEach>
    <c:if test="${empty featuredProducts}">
        <p class="muted">No products listed yet.</p>
    </c:if>
</div>

<%@ include file="WEB-INF/views/footer.jspf" %>