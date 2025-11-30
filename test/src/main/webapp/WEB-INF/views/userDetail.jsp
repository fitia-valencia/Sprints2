<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Détail Utilisateur</title>
</head>
<body>
    <h1>Détail Utilisateur</h1>
    <p><strong>Méthode:</strong> ${method}</p>
    <p><strong>Nom:</strong> ${user.name}</p>
    <p><strong>Email:</strong> ${user.email}</p>
    <p><strong>Âge:</strong> ${user.age}</p>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>