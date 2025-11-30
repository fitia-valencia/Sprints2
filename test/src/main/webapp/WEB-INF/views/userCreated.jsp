<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Utilisateur Créé</title>
</head>
<body>
    <h1>Utilisateur Créé</h1>
    <p><strong>Méthode:</strong> ${method}</p>
    <p><strong>Message:</strong> ${message}</p>
    <p><strong>Nom:</strong> ${user.name}</p>
    <p><strong>Email:</strong> ${user.email}</p>
    <p><strong>Âge:</strong> ${user.age}</p>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>