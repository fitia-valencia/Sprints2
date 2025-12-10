<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Arrays" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 8 - Résultats</title>
</head>
<body>

    <div>
        <h1> SPRINT 8 - RÉSULTATS</h1>
        <p><strong>Méthode utilisée:</strong> ${method}</p>
        <p><strong>Sprint:</strong> ${sprint}</p>
    </div>
    
    <div>
        <h2> Données reçues dans le Map (${formData.size()} entrées)</h2>
        
        <table>
            <tr>
                <th>Clé</th>
                <th>Valeur</th>
                <th>Type</th>
                <th>Explication</th>
            </tr>
            <%
                Map<String, Object> formData = (Map<String, Object>) request.getAttribute("formData");
                if (formData != null) {
                    for (Map.Entry<String, Object> entry : formData.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        String displayValue;
                        String typeClass;
                        String explanation = "";
                        
                        if (value == null) {
                            displayValue = "null";
                            typeClass = "type-null";
                            explanation = "Champ présent mais vide";
                        } else if (value instanceof String[]) {
                            String[] array = (String[]) value;
                            displayValue = Arrays.toString(array);
                            typeClass = "type-array";
                            explanation = "Tableau (longueur: " + array.length + ") - typique des checkbox multiples";
                        } else {
                            displayValue = value.toString();
                            typeClass = "type-string";
                            if ("true".equals(value) || "false".equals(value)) {
                                explanation = "Valeur booléenne - typique d'une checkbox unique";
                            }
                        }
            %>
            <tr>
                <td><strong><%= key %></strong></td>
                <td><span class="<%= typeClass %>"><%= displayValue %></span></td>
                <td>
                    <% 
                        if (value == null) {
                            out.print("null");
                        } else if (value instanceof String[]) {
                            out.print("String[]");
                        } else {
                            out.print(value.getClass().getSimpleName());
                        }
                    %>
                </td>
                <td><%= explanation %></td>
            </tr>
            <%
                    }
                }
            %>
        </table>
    </div>
    
    <div>
        <h2> Analyse des checkbox</h2>
        <ul>
            <li>
                <strong>Checkbox "accept":</strong>
                <%
                    if (formData != null && formData.containsKey("accept")) {
                        Object accept = formData.get("accept");
                        if (accept != null && "true".equals(accept)) {
                            out.print(" COCHÉE (valeur: " + accept + ")");
                        } else {
                            out.print(" NON COCHÉE (valeur: " + accept + ")");
                        }
                    } else {
                        out.print(" ABSENTE du Map (non cochée)");
                    }
                %>
            </li>
            <li>
                <strong>Centres d'intérêt:</strong>
                <%
                    if (formData != null && formData.containsKey("interests")) {
                        Object interests = formData.get("interests");
                        if (interests instanceof String[]) {
                            String[] interestsArray = (String[]) interests;
                            out.print("<ul>");
                            for (String interest : interestsArray) {
                                out.print("<li>" + interest + "</li>");
                            }
                            out.print("</ul>");
                        } else if (interests != null) {
                            out.print(interests);
                        }
                    } else {
                        out.print("Aucun sélectionné");
                    }
                %>
            </li>
        </ul>
    </div>
    
    <div>
        <a href="form">
             Nouveau test
        </a>
    </div>

</body>
</html>
