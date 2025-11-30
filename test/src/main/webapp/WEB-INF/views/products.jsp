<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="testapp.models.Product" %>
<html>
<head>
    <title>Liste des Produits</title>
</head>
<body>
    <h1>Nos Produits</h1>
    
    <table border="1">
        <tr>
            <th>Nom</th>
            <th>Prix</th>
        </tr>
        <% 
            List<Product> products = (List<Product>) request.getAttribute("products");
            for(Product product : products) { 
        %>
            <tr>
                <td><%= product.getName() %></td>
                <td>$<%= product.getPrice() %></td>
            </tr>
        <% } %>
    </table>
    
    <p><a href="/testapp">Retour à l'accueil</a></p>
</body>
</html>