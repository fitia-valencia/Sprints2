<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 8 bis - Binding automatique</title>
</head>
<body>
    <h1>SPRINT 8 bis - Binding automatique des objets</h1>

    <!-- <div>
        <h3>Objectif du sprint :</h3>
        <p>Pouvoir recevoir directement des objets Java dans les paramètres des méthodes du contrôleur,
        sans passer par un Map, avec binding automatique des paramètres du formulaire vers les attributs de l'objet.</p>
    </div> -->

    <!-- Test 1: Un seul objet -->
    <div>
        <h2>Test 1: Un seul objet (Emp)</h2>
        <form action="save-simple" method="post">
            <p><em>Les paramètres doivent correspondre aux attributs de l'objet :</em></p>

            <div>
                <label>emp.id:</label>
                <input type="number" name="id" placeholder="ID de l'employé">
            </div>

            <div>
                <label>emp.name:</label>
                <input type="text" name="name" placeholder="Nom de l'employé">
            </div>

            <div>
                <label>emp.department.id:</label>
                <input type="number" name="department.id" placeholder="ID du département">
            </div>

            <div>
                <label>emp.department.name:</label>
                <input type="text" name="department.name" placeholder="Nom du département">
            </div>

            <button type="submit">Tester (1 objet)</button>
        </form>
    </div>

    <!-- Test 2: Deux objets -->
    <div>
        <h2>Test 2: Deux objets (Emp + Dept)</h2>
        <form action="save-multiple" method="post">
            <p><em>Les paramètres peuvent être liés à différents objets :</em></p>

            <h3>Employé :</h3>
            <div>
                <label>emp.id:</label>
                <input type="number" name="id" placeholder="ID employé">
            </div>

            <div>
                <label>emp.name:</label>
                <input type="text" name="name" placeholder="Nom employé">
            </div>

            <h3>Département (objet séparé) :</h3>
            <div>
                <label>dept.id:</label>
                <input type="number" name="dept.id" placeholder="ID département">
            </div>

            <div>
                <label>dept.name:</label>
                <input type="text" name="dept.name" placeholder="Nom département">
            </div>

            <button type="submit">Tester (2 objets)</button>
        </form>
    </div>

    <!-- Test 3: Mélange objet et Map -->
    <div>
        <h2>Test 3: Mélange objet et Map</h2>
        <form action="save-mixed" method="post">
            <p><em>Certains paramètres vont dans l'objet, d'autres dans le Map :</em></p>

            <div>
                <label>emp.id:</label>
                <input type="number" name="id" placeholder="ID">
            </div>

            <div>
                <label>emp.name:</label>
                <input type="text" name="name" placeholder="Nom">
            </div>

            <div>
                <label>extra.email:</label>
                <input type="email" name="email" placeholder="Email">
            </div>

            <div>
                <label>extra.phone:</label>
                <input type="text" name="phone" placeholder="Téléphone">
            </div>

            <button type="submit">Tester (Mixte)</button>
        </form>
    </div>

    <!-- Test 4: Objet avec tableau -->
    <div>
        <h2>Test 4: Objet avec tableau</h2>
        <form action="save-array" method="post">
            <p><em>Gestion des tableaux avec notation [] :</em></p>

            <div>
                <label>emp.id:</label>
                <input type="number" name="id" placeholder="ID">
            </div>

            <div>
                <label>emp.name:</label>
                <input type="text" name="name" placeholder="Nom">
            </div>

            <div>
                <h4>Compétences (skills[]) :</h4>

                <div>
                    <label>skills[0]:</label>
                    <input type="text" name="skills" placeholder="Première compétence">
                </div>

                <div>
                    <label>skills[1]:</label>
                    <input type="text" name="skills" placeholder="Deuxième compétence">
                </div>

                <div>
                    <label>skills[2]:</label>
                    <input type="text" name="skills" placeholder="Troisième compétence">
                </div>

                <small>Les paramètres avec le même nom (skills) sont automatiquement regroupés en tableau.</small>
            </div>

            <button type="submit">Tester (Tableau)</button>
        </form>
    </div>

    <!-- Exemples de syntaxe -->
    <!-- <div>
        <h3>Exemples de syntaxe supportées :</h3>
        <ul>
            <li>name="id" → emp.setId(value)</li>
            <li>name="department.id" → emp.getDepartment().setId(value)</li>
            <li>name="dept.id" → dept.setId(value)</li>
            <li>name="skills" (plusieurs champs) → emp.setSkills(String[] values)</li>
        </ul>
    </div> -->

</body>
</html>
