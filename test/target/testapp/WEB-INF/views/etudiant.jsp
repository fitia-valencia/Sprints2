<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Étudiant ${id}</title>
</head>
<body>
    <h1>Fiche étudiant</h1>
    
    <h2>Informations:</h2>
    <ul>
        <li><strong>ID:</strong> ${id}</li>
        <li><strong>Nom:</strong> ${nom}</li>
        <li><strong>Âge:</strong> ${age} ans</li>
        <li><strong>Filière:</strong> ${filiere}</li>
    </ul>
    
    <p>
        <a href="/testapp/etudiant/${id}/notes">Voir les notes</a> | 
        <a href="/testapp/etudiant/${id}/cours/MATH">Voir cours MATH</a> |
        <a href="/testapp">Accueil</a>
    </p>
</body>
</html>