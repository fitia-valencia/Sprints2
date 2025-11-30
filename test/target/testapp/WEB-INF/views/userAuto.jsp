<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Utilisateur ${id}</title>
</head>
<body>
    <h1>Utilisateur (Auto)</h1>
    <p><strong>Méthode:</strong> ${method}</p>
    <p><strong>ID:</strong> ${id}</p>
    <p><strong>Nom:</strong> ${name}</p>
    
    <h3>Tests possibles:</h3>
    <ul>
        <li><a href="/testapp/auto/user/123">/auto/user/123</a></li>
        <li><a href="/testapp/auto/user/456?name=Alice">/auto/user/456?name=Alice</a></li>
    </ul>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>