<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Recherche Auto</title>
</head>
<body>
    <h1>Recherche (Auto)</h1>
    <p><strong>Méthode:</strong> ${method}</p>
    <p><strong>Query:</strong> ${query}</p>
    <p><strong>Page:</strong> ${page}</p>
    <p><strong>Résultats:</strong> ${results}</p>
    
    <h3>Tests possibles:</h3>
    <ul>
        <li><a href="/testapp/auto/search?query=java&page=1">/auto/search?query=java&page=1</a></li>
        <li><a href="/testapp/auto/search?query=spring&page=2">/auto/search?query=spring&page=2</a></li>
    </ul>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>