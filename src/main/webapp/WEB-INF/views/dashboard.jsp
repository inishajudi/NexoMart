<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard — NexoMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Welcome, <c:out value="${sessionScope.userName}" /></h1>
        <p>Role: <c:out value="${sessionScope.userRole}" /></p>
        <p><em>Product browsing, cart, and checkout ship in Week 2.</em></p>

        <form method="post" action="${pageContext.request.contextPath}/logout">
            <button type="submit" class="btn btn-secondary">Log out</button>
        </form>
    </div>
</body>
</html>
