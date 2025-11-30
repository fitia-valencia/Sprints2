<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mixed Params</title>
</head>
<body>
    <h1>Paramètres Mixtes (Auto)</h1>
    <p><strong>Méthode:</strong> ${method}</p>
    <p><strong>ID:</strong> ${id}</p>
    <p><strong>Format:</strong> ${format}</p>
    <p><strong>Limit:</strong> ${limit}</p>
    <p><strong>Message:</strong> ${message}</p>
    
    <h3>Tests possibles:</h3>
    <ul>
        <li><a href="/testapp/auto/mixed/123/details?format=json&limit=10">/auto/mixed/123/details?format=json&limit=10</a></li>
        <li><a href="/testapp/auto/mixed/456/details?format=xml&limit=5">/auto/mixed/456/details?format=xml&limit=5</a></li>
    </ul>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>