<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cours ${coursId}</title>
</head>
<body>
    <h1>Cours: ${coursNom}</h1>
    
    <h2>Détails du cours:</h2>
    <ul>
        <li><strong>ID du cours:</strong> ${coursId}</li>
        <li><strong>Nom du cours:</strong> ${coursNom}</li>
        <li><strong>Présence:</strong> ${presence}</li>
        <li><strong>Étudiant ID:</strong> ${etudiantId}</li>
    </ul>
    
    <h3>Actions:</h3>
    <p>
        <a href="/testapp/etudiant/${etudiantId}">Retour au profil étudiant</a> |
        <a href="/testapp/etudiant/${etudiantId}/notes">Voir les notes</a> |
        <a href="/testapp">Accueil</a>
    </p>
    
    <h3>Test d'autres cours:</h3>
    <p>
        <a href="/testapp/etudiant/${etudiantId}/cours/MATH">Cours MATH</a> |
        <a href="/testapp/etudiant/${etudiantId}/cours/JAVA">Cours JAVA</a> |
        <a href="/testapp/etudiant/${etudiantId}/cours/WEB">Cours WEB</a>
    </p>
</body>
</html>