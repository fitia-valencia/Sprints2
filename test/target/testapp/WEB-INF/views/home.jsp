<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
    <h1>${title}</h1>
    <p>${message}</p>
    
    <h2>Liste des utilisateurs:</h2>
    <ul>
        <% String[] users = (String[]) request.getAttribute("users"); %>
        <% for(String user : users) { %>
            <li><%= user %></li>
        <% } %>
    </ul>
    
    <p><a href="/testapp">Retour à l'accueil</a></p>
</body>
</html>