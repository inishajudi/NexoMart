<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.nexomart.app.filter.AuthFilter" %>
<%@ page import="com.nexomart.app.model.User" %>
<%
    /* ── LOGIN-FIRST: redirect to /login if no session ── */
    HttpSession s = request.getSession(false);
    User currentUser = (s == null) ? null : (User) s.getAttribute(AuthFilter.SESSION_USER_ATTR);
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<%@ include file="WEB-INF/views/header.jspf" %>

<section style="padding:56px 0 40px;">
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
    <a href="<c:url value='/products?category=Skin Care'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 0 20A10 10 0 0 0 12 2z"/><path d="M8 14s1.5 2 4 2 4-2 4-2"/><path d="M9 9h.01M15 9h.01"/></svg>
        Skin Care
    </a>
    <a href="<c:url value='/products?category=Beauty'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
        Beauty
    </a>
    <a href="<c:url value='/products'/>" class="category-tile">
        <svg class="icon-lg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
        View all
    </a>
</div>

<div class="section-heading">
    <h2>All products</h2>
    <a href="<c:url value='/products'/>">Browse everything</a>
</div>
<div class="product-grid">
    <c:forEach var="product" items="${featuredProducts}">
        <a href="<c:url value='/products/view'><c:param name='id' value='${product.id}'/></c:url>"
           class="card" style="text-decoration:none; display:block; padding:0; overflow:hidden;">
            <div class="product-img-wrap">
                <c:choose>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" loading="lazy">
                    </c:when>
                    <c:otherwise>
                        <div class="no-img" style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;color:var(--text-faint);font-size:13px;background:var(--surface-2);">No image</div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="product-info">
                <div class="product-category"><c:out value="${product.category}"/></div>
                <div class="product-name"><c:out value="${product.name}"/></div>
                <span class="price"><c:out value="${product.price}"/></span>
            </div>
        </a>
    </c:forEach>
    <c:if test="${empty featuredProducts}">
        <p class="muted">No products listed yet.</p>
    </c:if>
</div>

<%@ include file="WEB-INF/views/footer.jspf" %>