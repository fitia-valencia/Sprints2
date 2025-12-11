<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="testapp.models.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 8 bis - Résultats</title>
</head>
<body>
    <h1>SPRINT 8 bis - RÉSULTATS</h1>
    <p><strong>Test effectué :</strong> ${test}</p>

    <% 
        Emp emp = (Emp) request.getAttribute("emp");
        if (emp != null) { 
    %>
    <h2>Objet Emp reçu :</h2>
    <table border="1" cellpadding="5">
        <tr>
            <th>Attribut</th>
            <th>Valeur</th>
            <th>Type</th>
        </tr>
        <tr>
            <td>id</td>
            <td><%= emp.getId() %></td>
            <td><%= emp.getId() != 0 ? "int" : "non défini" %></td>
        </tr>
        <tr>
            <td>name</td>
            <td><%= emp.getName() != null ? emp.getName() : "null" %></td>
            <td>String</td>
        </tr>
        <tr>
            <td>department</td>
            <td>
                <% 
                    Dept dept = emp.getDepartment();
                    if (dept != null) {
                        out.print("Dept{id=" + dept.getId() + ", name='" + dept.getName() + "'}");
                    } else {
                        out.print("null");
                    }
                %>
            </td>
            <td><%= dept != null ? "Dept" : "null" %></td>
        </tr>
        <tr>
            <td>skills</td>
            <td>
                <% 
                    String[] skills = emp.getSkills();
                    if (skills != null && skills.length > 0) {
                        out.print("[");
                        for (int i = 0; i < skills.length; i++) {
                            if (i > 0) out.print(", ");
                            out.print(skills[i]);
                        }
                        out.print("]");
                    } else {
                        out.print("null ou vide");
                    }
                %>
            </td>
            <td><%= skills != null ? "String[]" : "null" %></td>
        </tr>
    </table>
    <% } %>

    <% 
        Dept deptSeparate = (Dept) request.getAttribute("dept");
        if (deptSeparate != null) { 
    %>
    <h2>Objet Dept séparé reçu :</h2>
    <table border="1" cellpadding="5">
        <tr>
            <th>Attribut</th>
            <th>Valeur</th>
        </tr>
        <tr>
            <td>id</td>
            <td><%= deptSeparate.getId() %></td>
        </tr>
        <tr>
            <td>name</td>
            <td><%= deptSeparate.getName() %></td>
        </tr>
    </table>
    <% } %>

    <%
        java.util.Map<String, Object> extraData = (java.util.Map<String, Object>) request.getAttribute("extraData");
        if (extraData != null && !extraData.isEmpty()) {
    %>
    <h2>Map extraData reçu :</h2>
    <table border="1" cellpadding="5">
        <tr>
            <th>Clé</th>
            <th>Valeur</th>
            <th>Type</th>
        </tr>
        <%
            for (java.util.Map.Entry<String, Object> entry : extraData.entrySet()) {
                Object value = entry.getValue();
        %>
        <tr>
            <td><%= entry.getKey() %></td>
            <td>
                <%
                    if (value instanceof String[]) {
                        String[] array = (String[]) value;
                        out.print(java.util.Arrays.toString(array));
                    } else {
                        out.print(value);
                    }
                %>
            </td>
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
        </tr>
        <% } %>
    </table>
    <% } %>

    <p>
        <a href="test">Nouveau test</a>
    </p>
</body>
</html>
