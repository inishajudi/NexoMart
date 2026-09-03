<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Log in — NexoMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container form-container">
        <h1>Log in to NexoMart</h1>

        <c:if test="${not empty error}">
            <p class="error"><c:out value="${error}" /></p>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="<c:out value='${email}' />" required>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>

            <button type="submit" class="btn">Log in</button>
        </form>

        <p>New to NexoMart? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
    </div>
</body>
</html>
