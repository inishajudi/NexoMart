<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register — NexoMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container form-container">
        <h1>Create your NexoMart account</h1>

        <c:if test="${not empty error}">
            <p class="error"><c:out value="${error}" /></p>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <label for="name">Name</label>
            <input type="text" id="name" name="name" value="<c:out value='${name}' />" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="<c:out value='${email}' />" required>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" minlength="8" required>

            <label for="role">I am a</label>
            <select id="role" name="role" required>
                <option value="BUYER">Buyer</option>
                <option value="SELLER">Seller</option>
            </select>

            <button type="submit" class="btn">Register</button>
        </form>

        <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
    </div>
</body>
</html>
