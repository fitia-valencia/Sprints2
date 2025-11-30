<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${titre}</title>
</head>
<body>
    <h1>${titre}</h1>
    <p>${message}</p>
    <p>Le nombre est : ${nombre}</p>
    
    <h2>Données reçues du contrôleur :</h2>
    <ul>
        <li>Titre: <strong>${titre}</strong></li>
        <li>Message: <strong>${message}</strong></li>
        <li>Nombre: <strong>${nombre}</strong></li>
    </ul>
    
    <p><a href="/testapp">Retour</a></p>
</body>
</html>