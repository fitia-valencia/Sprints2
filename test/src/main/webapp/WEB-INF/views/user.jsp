<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Profil Utilisateur</title>
</head>
<body>
    <h1>Profil Utilisateur</h1>
    
    <h2>Informations:</h2>
    <ul>
        <li><strong>Nom:</strong> ${name}</li>
        <li><strong>Âge:</strong> ${age} ans</li>
        <li><strong>Actif:</strong> ${active ? 'Oui' : 'Non'}</li>
        <li><strong>Statut:</strong> ${status}</li>
    </ul>

    <p><a href="/testapp/formulaire.html">Nouveau formulaire</a></p>
</body>
</html>