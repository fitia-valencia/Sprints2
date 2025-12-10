<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 8 - Test Map&lt;String, Object&gt;</title>
</head>
<body>

    <h1>SPRINT 8 - Test Map&lt;String, Object&gt;</h1>
    
    <!-- Test 1: Formulaire avec Map uniquement -->
    <div>
        <h2>Test 1: Méthode avec Map&lt;String, Object&gt;</h2>
        <form action="process" method="post">
            <div>
                <label>Nom complet:</label>
                <input type="text" name="fullname" placeholder="Votre nom">
            </div>
            
            <div>
                <label>Email:</label>
                <input type="email" name="email" placeholder="email@example.com">
            </div>
            
            <div>
                <label>Âge:</label>
                <input type="number" name="age" min="1" max="120">
            </div>
            
            <div>
                <label>J'accepte les conditions:</label>
                <input type="checkbox" name="accept" value="true">
                <small>(Checkbox unique - valeur sera "true" si cochée, absente si non cochée)</small>
            </div>
            
            <div>
                <label>Vos centres d'intérêt:</label>
                <div>
                    <input type="checkbox" name="interests" value="sport"> Sport<br>
                    <input type="checkbox" name="interests" value="music"> Musique<br>
                    <input type="checkbox" name="interests" value="tech"> Technologie<br>
                    <input type="checkbox" name="interests" value="art"> Art<br>
                    <input type="checkbox" name="interests" value="travel"> Voyage<br>
                </div>
            </div>
            
            <div>
                <label>Niveau d'étude:</label>
                <select name="education">
                    <option value="">-- Sélectionnez --</option>
                    <option value="bac">Bac</option>
                    <option value="licence">Licence</option>
                    <option value="master">Master</option>
                    <option value="doctorat">Doctorat</option>
                </select>
            </div>
            
            <div>
                <label>Commentaires:</label>
                <textarea name="comments" rows="4" cols="50"></textarea>
            </div>
            
            <button type="submit">
                Envoyer (Sprint 8 - Map)
            </button>
        </form>
    </div>

</body>
</html>
