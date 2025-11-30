<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Notes Étudiant ${etudiantId}</title>
</head>
<body>
    <h1>Notes de ${nom}</h1>
    
    <h2>Bulletin:</h2>
    <ul>
        <% String[] notes = (String[]) request.getAttribute("notes"); %>
        <% for(String note : notes) { %>
            <li><%= note %></li>
        <% } %>
    </ul>
    
    <p><a href="/testapp/etudiant/${etudiantId}">Retour au profil</a></p>
</body>
</html>