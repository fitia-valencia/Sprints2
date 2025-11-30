<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Test Résultat</title>
</head>
<body>
    <h1>Test Résultat</h1>
    <p><strong>Message:</strong> ${message}</p>
    
    <h2>Données reçues:</h2>
    <ul>
        <c:if test="${not empty id}"><li>ID: ${id}</li></c:if>
        <c:if test="${not empty name}"><li>Name: ${name}</li></c:if>
        <c:if test="${not empty text}"><li>Text: ${text}</li></c:if>
        <c:if test="${not empty number}"><li>Number: ${number}</li></c:if>
        <c:if test="${not empty flag}"><li>Flag: ${flag}</li></c:if>
    </ul>
    
    <h3>Tests de débogage:</h3>
    <ul>
        <li><a href="/testapp/test/missing-param">/test/missing-param (id manquant)</a></li>
        <li><a href="/testapp/test/missing-param?id=123">/test/missing-param?id=123</a></li>
        <li><a href="/testapp/test/convert-error?text=hello&number=abc&flag=true">/test/convert-error?text=hello&number=abc&flag=true (erreur conversion)</a></li>
        <li><a href="/testapp/test/convert-error?text=hello&number=42&flag=yes">/test/convert-error?text=hello&number=42&flag=yes</a></li>
    </ul>
    
    <p><a href="/testapp">Accueil</a></p>
</body>
</html>