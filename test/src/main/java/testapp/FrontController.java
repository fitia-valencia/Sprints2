package testapp;

import com.monframework.scanner.ControllerScanner;
import com.monframework.scanner.RouteInfo;
import com.monframework.ModelView;
import com.monframework.annotation.PathVariable;
import com.monframework.annotation.RequestParam;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontController extends HttpServlet {

    private ControllerScanner scanner;

    @Override
    public void init() throws ServletException {
        System.out.println("=== INITIALISATION DE L'APPLICATION ===");

        scanner = new ControllerScanner();
        scanner.scanControllers("testapp");

        // Stocker le scanner dans le contexte servlet
        getServletContext().setAttribute("routeScanner", scanner);

        // Afficher toutes les routes disponibles
        displayAllRoutes();

        System.out.println("=== INITIALISATION TERMINÉE ===");
    }

    // Ajoutez cette méthode dans FrontController.java
    private Map<String, Object> buildDataMap(HttpServletRequest request, Map<String, String> pathVariables) {
        Map<String, Object> dataMap = new HashMap<>();

        // Récupérer tous les paramètres de la requête
        Map<String, String[]> requestParams = request.getParameterMap();

        // Ajouter les paramètres de requête
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String[] values = entry.getValue();
            if (values == null || values.length == 0) {
                continue;
            }

            if (values.length == 1) {
                // Cas d'une seule valeur
                dataMap.put(entry.getKey(), values[0]);
            } else {
                // Cas multiple (checkbox, select multiple)
                dataMap.put(entry.getKey(), values);
            }
        }

        // Ajouter les variables de chemin
        if (pathVariables != null) {
            dataMap.putAll(pathVariables);
        }

        // Gestion spéciale pour les checkbox non cochées
        // (Optionnel: ajouter explicitement les checkbox manquantes avec false)
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!dataMap.containsKey(paramName)) {
                dataMap.put(paramName, null);
            }
        }

        System.out.println("Map créée avec " + dataMap.size() + " entrées");
        return dataMap;
    }

    private void displayAllRoutes() {
        System.out.println("\n LISTE DES ROUTES DISPONIBLES:");
        System.out.println("=================================");

        List<RouteInfo> routes = scanner.getRoutes();

        if (routes.isEmpty()) {
            System.out.println("Aucune route trouvée!");
            return;
        }

        for (RouteInfo route : scanner.getRoutes()) {
            System.out.println(route.getHttpMethod() + " " + route.getUrlPattern() +
                    " -> " + route.getMethod().getDeclaringClass().getSimpleName() + "." + route.getMethod().getName()
                    + "()");
        }

        System.out.println("Total: " + routes.size() + " route(s) configurée(s)");
        System.out.println("=================================\n");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestedUrl = request.getRequestURI().substring(request.getContextPath().length());
        if (isStaticResource(requestedUrl)) {
            serveStaticResource(requestedUrl, request, response);
            return;
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html; charset=UTF-8");

        System.out.println(" Requête reçue: " + requestedUrl);

        Map<String, String[]> params = request.getParameterMap();
        if (!params.isEmpty()) {
            System.out.println("Paramètres GET:");
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                System.out.println("   - " + entry.getKey() + " = " + String.join(", ", entry.getValue()));
            }
        }

        RouteInfo routeInfo = scanner.findMatchingRoute(requestedUrl, "GET");

        if (routeInfo != null) {
            try {
                Method method = routeInfo.getMethod();
                Object controllerInstance = method.getDeclaringClass().newInstance();

                System.out.println("Exécution: " + method.getDeclaringClass().getSimpleName() + "." + method.getName());

                // Préparer les arguments pour la méthode
                Object[] args = prepareArguments(routeInfo, requestedUrl, request);

                // Exécuter la méthode avec les arguments
                Object result = method.invoke(controllerInstance, args);

                // Gérer le retour
                if (result instanceof ModelView) {
                    handleModelView((ModelView) result, request, response);
                } else {
                    handleMethodResult(result, out, method);
                }

            } catch (Exception e) {
                System.out.println("ERREUR: " + e.getMessage());
                e.printStackTrace();
                out.println("<h1>Erreur d'exécution</h1><pre>" + e.getMessage() + "</pre>");
            }
        } else {
            System.out.println("URL non trouvée: " + requestedUrl);
            out.println("<h1>404 - URL non trouvée</h1>");
            out.println("<p>Aucune route trouvée pour: " + requestedUrl + "</p>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String actualMethod = getActualHttpMethod(request);
        if (!"POST".equals(actualMethod)) {
            processRequest(request, response, actualMethod);
        } else {
            processRequest(request, response, "POST");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String actualMethod = getActualHttpMethod(request);
        processRequest(request, response, actualMethod);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String actualMethod = getActualHttpMethod(request);
        processRequest(request, response, actualMethod);
    }

    private Object convertValue(String stringValue, Class<?> targetType) {
        // Si la valeur est null, gérer selon le type
        if (stringValue == null) {
            if (targetType.isPrimitive()) {
                // Pour les types primitifs, retourner la valeur par défaut
                if (targetType == int.class)
                    return 0;
                if (targetType == long.class)
                    return 0L;
                if (targetType == double.class)
                    return 0.0;
                if (targetType == boolean.class)
                    return false;
                if (targetType == float.class)
                    return 0.0f;
                if (targetType == byte.class)
                    return (byte) 0;
                if (targetType == short.class)
                    return (short) 0;
                if (targetType == char.class)
                    return '\0';
            }
            return null; // Pour les types objets
        }

        try {
            if (targetType == String.class) {
                return stringValue;
            } else if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(stringValue);
            } else if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(stringValue);
            } else if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(stringValue);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(stringValue);
            } else if (targetType == float.class || targetType == Float.class) {
                return Float.parseFloat(stringValue);
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur conversion: " + stringValue + " vers " + targetType.getSimpleName());
            // En cas d'erreur, retourner la valeur par défaut pour les primitifs
            if (targetType.isPrimitive()) {
                if (targetType == int.class)
                    return 0;
                if (targetType == long.class)
                    return 0L;
                if (targetType == double.class)
                    return 0.0;
                if (targetType == boolean.class)
                    return false;
                if (targetType == float.class)
                    return 0.0f;
            }
            return null;
        }

        return stringValue;
    }

    private Object[] prepareArguments(RouteInfo routeInfo, String requestedUrl, HttpServletRequest request) {
        Parameter[] parameters = routeInfo.getParameters();
        Object[] args = new Object[parameters.length];

        // Extraire les variables du path
        Map<String, String> pathVariables = routeInfo.extractPathVariablesValues(requestedUrl);

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = param.getName();
            Class<?> paramType = param.getType();

            System.out.println(
                    "Traitement paramètre " + i + ": " + paramName + " (type: " + paramType.getSimpleName() + ")");

            // SPRINT 8: Vérifier si c'est un Map<String, Object> ou Map
            if (Map.class.isAssignableFrom(paramType)) {
                System.out.println("  -> Détection Map: injection de toutes les données");
                args[i] = buildDataMap(request, pathVariables);
                System.out.println("  -> Map injectée avec " + ((Map<?, ?>) args[i]).size() + " entrées");
                continue;
            }

            // Continuer avec le traitement existant pour les autres types
            if (param.isAnnotationPresent(PathVariable.class)) {
                PathVariable pathAnnotation = param.getAnnotation(PathVariable.class);
                String variableName = pathAnnotation.value();
                String stringValue = pathVariables.get(variableName);

                args[i] = convertValue(stringValue, param.getType());
                System.out.println("    @PathVariable " + variableName + " = " + args[i]);

            } else if (param.isAnnotationPresent(RequestParam.class)) {
                // Gestion RequestParam (sprint 6bis)
                RequestParam requestAnnotation = param.getAnnotation(RequestParam.class);
                String defaultValue = requestAnnotation.defaultValue();

                String stringValue = request.getParameter(paramName);
                if (stringValue == null && !defaultValue.isEmpty()) {
                    stringValue = defaultValue;
                    System.out.println("    @RequestParam " + paramName + " (valeur par défaut) = " + stringValue);
                } else if (stringValue != null) {
                    System.out.println("    @RequestParam " + paramName + " = " + stringValue);
                } else {
                    System.out.println("     @RequestParam " + paramName + " = null (non fourni)");
                }

                args[i] = convertValue(stringValue, param.getType());

            } else {
                // SPRINT 6ter: Injection automatique par nom
                String stringValue = null;

                // 1. Chercher dans les variables de chemin d'abord
                if (pathVariables.containsKey(paramName)) {
                    stringValue = pathVariables.get(paramName);
                    System.out.println("     Trouvé dans pathVariables: " + stringValue);
                }
                // 2. Chercher dans les paramètres de requête
                else if (request.getParameter(paramName) != null) {
                    stringValue = request.getParameter(paramName);
                    System.out.println("     Trouvé dans query parameters: " + stringValue);
                }
                // 3. Si pas trouvé, essayer avec le nom de la variable dans l'URL
                else {
                    stringValue = extractFromUrlPattern(routeInfo.getUrlPattern(), requestedUrl, paramName);
                    if (stringValue != null) {
                        System.out.println("     Extraite du pattern URL: " + stringValue);
                    } else {
                        System.out.println("       Paramètre '" + paramName + "' non trouvé");
                    }
                }

                // Convertir la valeur
                args[i] = convertValue(stringValue, paramType);
                System.out.println("     Valeur finale: " + args[i] + " (type: " + paramType.getSimpleName() + ")");
            }
        }

        return args;
    }

    private String extractFromUrlPattern(String urlPattern, String actualUrl, String paramName) {
        // Si le pattern contient {paramName}, extraire la valeur
        if (urlPattern.contains("{" + paramName + "}")) {
            // Convertir le pattern en regex
            String regex = urlPattern.replaceAll("\\{" + paramName + "\\}", "([^/]+)");
            regex = regex.replaceAll("\\{.*?\\}", "[^/]+");
            regex = "^" + regex + "$";

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(actualUrl);

            if (matcher.matches()) {
                // Trouver le groupe qui correspond à notre paramètre
                String[] patternParts = urlPattern.split("/");
                String[] actualParts = actualUrl.split("/");

                for (int i = 0; i < patternParts.length && i < actualParts.length; i++) {
                    if (patternParts[i].equals("{" + paramName + "}")) {
                        return actualParts[i];
                    }
                }
            }
        }
        return null;
    }

    private void handleModelView(ModelView modelView, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String viewName = modelView.getView();
        System.out.println("ModelView détecté - Vue: " + viewName);
        System.out.println("Données: " + modelView.getData());

        // SPRINT 8: Ajouter aussi toutes les données si elles existent dans un Map
        // (Le contrôleur peut avoir ajouté des données via addAllObjects)

        // Ajouter les données à la requête
        for (Map.Entry<String, Object> entry : modelView.getData().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
            System.out.println("   -> " + entry.getKey() + " = " + entry.getValue());
        }

        // Forward vers la JSP
        String jspPath = "/WEB-INF/views/" + viewName;
        System.out.println("Forward vers: " + jspPath);

        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);
        dispatcher.forward(request, response);
    }

    private boolean isStaticResource(String url) {
        return url.endsWith(".html") ||
                url.endsWith(".css") ||
                url.endsWith(".js") ||
                url.endsWith(".png") ||
                url.endsWith(".jpg") ||
                url.equals("/formulaire.html");
    }

    private void serveStaticResource(String resourcePath, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Ressource statique: " + resourcePath);

        // Déléguer au servlet par défaut de Tomcat
        getServletContext().getNamedDispatcher("default").forward(request, response);
    }

    private void handleMethodResult(Object result, PrintWriter out, Method method) {
        System.out.println("Affichage direct du résultat...");

        out.println("<html><head><title>Résultat</title></head><body>");
        out.println("<h1>Méthode exécutée avec succès</h1>");
        out.println("<p><strong>Méthode:</strong> " + method.getDeclaringClass().getSimpleName() + "."
                + method.getName() + "()</p>");

        if (result != null) {
            String resultType = result.getClass().getSimpleName();
            System.out.println("Type détecté: " + resultType);

            out.println("<p><strong>Type de retour:</strong> " + resultType + "</p>");
            out.println("<div style='background: #f5f5f5; padding: 15px; border-radius: 5px;'>");
            out.println("<strong>Résultat:</strong><br>");

            if (result instanceof String) {
                System.out.println("C'est une String - Affichage direct");
                out.println("<pre>" + result + "</pre>");
            } else {
                System.out.println("Autre type - Utilisation de toString()");
                out.println("<pre>" + result.toString() + "</pre>");
            }

            out.println("</div>");
        } else {
            System.out.println("La méthode a retourné null");
            out.println("<p><em>La méthode a retourné null</em></p>");
        }

        out.println("<br><a href='/testapp'>Retour à l'accueil</a>");
        out.println("</body></html>");

        System.out.println("Affichage terminé");
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String httpMethod)
            throws ServletException, IOException {

        String requestedUrl = request.getRequestURI().substring(request.getContextPath().length());
        PrintWriter out = response.getWriter();
        response.setContentType("text/html; charset=UTF-8");

        // ✅ SANS EMOJIS
        System.out.println("Requête " + httpMethod + " reçue: " + requestedUrl);

        // Afficher les paramètres
        Map<String, String[]> params = request.getParameterMap();
        if (!params.isEmpty()) {
            System.out.println("Paramètres " + httpMethod + ":");
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                System.out.println("   - " + entry.getKey() + " = " + String.join(", ", entry.getValue()));
            }
        }

        // Trouver la route avec la méthode HTTP
        RouteInfo routeInfo = scanner.findMatchingRoute(requestedUrl, httpMethod);

        if (routeInfo != null) {
            try {
                Method method = routeInfo.getMethod();
                Object controllerInstance = method.getDeclaringClass().newInstance();

                System.out.println("Execution: " + method.getDeclaringClass().getSimpleName() + "." + method.getName());

                // Préparer les arguments
                Object[] args = prepareArguments(routeInfo, requestedUrl, request);

                // Exécuter la méthode
                Object result = method.invoke(controllerInstance, args);

                // Gérer le retour
                if (result instanceof ModelView) {
                    handleModelView((ModelView) result, request, response);
                } else {
                    handleMethodResult(result, out, method);
                }

            } catch (Exception e) {
                System.out.println("ERREUR: " + e.getMessage());
                e.printStackTrace();
                out.println("<h1>Erreur d'exécution</h1><pre>" + e.getMessage() + "</pre>");
            }
        } else {
            System.out.println("Route non trouvée: " + httpMethod + " " + requestedUrl);
            out.println("<h1>404 - Route non trouvée</h1>");
            out.println("<p>Aucune route trouvée pour: " + httpMethod + " " + requestedUrl + "</p>");
            displayAvailableRoutes(out, httpMethod, requestedUrl);
        }
    }

    private void displayAvailableRoutes(PrintWriter out, String requestedMethod, String requestedUrl) {
        out.println("<h3>Routes disponibles pour " + requestedMethod + ":</h3>");
        out.println("<ul>");
        for (RouteInfo route : scanner.getRoutes()) {
            if (route.getHttpMethod().equalsIgnoreCase(requestedMethod)) {
                out.println("<li>" + route.getUrlPattern() + "</li>");
            }
        }
        out.println("</ul>");
    }

    private String getActualHttpMethod(HttpServletRequest request) {
        String method = request.getMethod();

        // Support pour les méthodes PUT/DELETE via paramètre _method
        if ("POST".equalsIgnoreCase(method)) {
            String hiddenMethod = request.getParameter("_method");
            if (hiddenMethod != null) {
                return hiddenMethod.toUpperCase();
            }
        }

        return method;
    }
}