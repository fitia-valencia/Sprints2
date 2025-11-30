<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Filtres Produits</title>
</head>
<body>
    <h1>Filtres Appliqués</h1>
    
    <h2>Critères de filtrage:</h2>
    <ul>
        <li><strong>Catégorie:</strong> ${category}</li>
        <li><strong>Prix min:</strong> $${minPrice}</li>
        <li><strong>Prix max:</strong> $${maxPrice}</li>
        <li><strong>En stock seulement:</strong> ${inStock ? 'Oui' : 'Non'}</li>
    </ul>

    <p><strong>Message:</strong> ${message}</p>

    <p><a href="/testapp/formulaire.html">Nouveau filtre</a></p>
</body>
</html>