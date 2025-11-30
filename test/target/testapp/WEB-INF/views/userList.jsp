<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Liste des Utilisateurs</title>
</head>
<body>
    <h1>Liste des Utilisateurs</h1>
    <p><strong>Méthode:</strong> ${method}</p>
    
    <h2>Utilisateurs:</h2>
    <ul>
        <c:forEach items="${users}" var="user">
            <li>
                <strong>${user.name}</strong> - ${user.email} - ${user.age} ans
            </li>
        </c:forEach>
    </ul>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>