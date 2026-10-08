<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>

<style>
    .page-title { font-family: 'DM Serif Display', Georgia, serif; font-weight: 400; font-size: 36px; color: #2b2418; margin: 32px 0 20px; }
    .wishlist-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; }
    .wl-card { background: #fbf6ea; border: 1px solid #e3d5b8; border-radius: 16px; padding: 16px; box-shadow: 0 4px 16px rgba(120,88,30,0.06); }
    .wl-card img { width: 100%; height: 180px; object-fit: cover; border-radius: 10px; margin-bottom: 12px; }
    .wl-name { font-weight: 600; color: #2b2418; margin-bottom: 4px; }
    .wl-price { color: #9a6a24; font-weight: 700; font-size: 16px; margin-bottom: 12px; }
    .wl-actions { display: flex; gap: 8px; }
    .btn-view { background: #9a6a24; color: #fff; padding: 6px 14px; border-radius: 999px; text-decoration: none; font-size: 13px; font-weight: 600; }
    .btn-remove { background: transparent; border: 1px solid #c0a070; color: #7a5020; padding: 6px 14px; border-radius: 999px; font-size: 13px; font-weight: 600; cursor: pointer; }
    .btn-remove:hover { background: #f5e6cc; }
    .empty-note { color: #7a6b52; }
</style>

<h2 class="page-title">My Wishlist</h2>

<c:choose>
    <c:when test="${empty items}">
        <div class="sand-card"><p class="empty-note">Your wishlist is empty. Browse products and save ones you like!</p></div>
    </c:when>
    <c:otherwise>
        <div class="wishlist-grid">
            <c:forEach var="p" items="${items}">
                <div class="wl-card">
                    <img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>"
                         onerror="this.style.display='none'"/>
                    <div class="wl-name"><c:out value="${p.name}"/></div>
                    <div class="wl-price">&#8377;<fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2"/></div>
                    <div class="wl-actions">
                        <a href="${pageContext.request.contextPath}/products/${p.id}" class="btn-view">View</a>
                        <form method="post" action="${pageContext.request.contextPath}/wishlist" style="margin:0;">
                            <input type="hidden" name="productId" value="${p.id}"/>
                            <input type="hidden" name="action" value="remove"/>
                            <button type="submit" class="btn-remove">Remove</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>