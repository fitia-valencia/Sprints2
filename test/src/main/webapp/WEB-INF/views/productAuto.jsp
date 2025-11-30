<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Produit ${productId}</title>
</head>
<body>
    <h1>Produit (Auto)</h1>
    <p><strong>Méthode:</strong> ${method}</p>
    <p><strong>ID Produit:</strong> ${productId}</p>
    <p><strong>Catégorie:</strong> ${category}</p>
    <p><strong>Nom:</strong> ${productName}</p>
    
    <h3>Tests possibles:</h3>
    <ul>
        <li><a href="/testapp/auto/product/111/category/electronics">/auto/product/111/category/electronics</a></li>
        <li><a href="/testapp/auto/product/222/category/books">/auto/product/222/category/books</a></li>
    </ul>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>