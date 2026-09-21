<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jspf" %>

<div class="card" style="max-width:480px; margin:60px auto; text-align:center;">
    <h2>Something went wrong</h2>
    <p class="muted">An unexpected error occurred on our end. Please try again in a moment.</p>
    <a href="<c:url value='/products'/>" class="btn" style="margin-top:12px; display:inline-block;">Back to marketplace</a>
</div>

<%@ include file="footer.jspf" %>