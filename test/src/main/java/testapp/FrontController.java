package testapp;

import com.monframework.scanner.ControllerScanner;
import com.monframework.scanner.RouteInfo;
import com.monframework.ModelView;
import com.monframework.annotation.PathVariable;
import com.monframework.annotation.RequestParam;
import com.monframework.session.MySession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.monframework.annotation.JsonAPI;
import com.monframework.annotation.RestController;
import com.monframework.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@MultipartConfig(location = "/tmp", maxFileSize = 10 * 1024 * 1024, // 10MB
        maxRequestSize = 50 * 1024 * 1024, // 50MB
        fileSizeThreshold = 1024 * 1024 // 1MB
)

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
                Object[] args = prepareArguments(routeInfo, requestedUrl, request, null);

                // Exécuter la méthode avec les arguments
                Object result = method.invoke(controllerInstance, args);

                // Gérer le retour
                if (result instanceof ModelView) {
                    handleModelView((ModelView) result, request, response);
                } else {
                    handleMethodResult(result, out, method, response);
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

    private Object[] prepareArguments(RouteInfo routeInfo, String requestedUrl,
            HttpServletRequest request,
            Map<String, Object> multipartData) {

        Parameter[] parameters = routeInfo.getParameters();
        Object[] args = new Object[parameters.length];

        // Extraire les variables du path
        Map<String, String> pathVariables = routeInfo.extractPathVariablesValues(requestedUrl);

        // sprint11
        Map<String, String[]> requestParams = multipartData != null
                ? extractTextParametersFromMultipart(multipartData)
                : request.getParameterMap();

        Map<String, List<String>> paramPatterns = analyzeParameterPatterns(requestParams);

        for (int i = 0; i < parameters.length; i++) {

            Parameter param = parameters[i];
            String paramName = param.getName();
            Class<?> paramType = param.getType();

            System.out.println("DEBUG - Traitement paramètre " + i + ": " + paramName +
                    " (type: " + paramType.getSimpleName() + ")");

            if (paramType == MySession.class) {
                System.out.println("  -> Injection MySession");
                args[i] = new MySession(request.getSession());
                continue;
            }

            // SPRINT 10: Gestion des fichiers uploadés
            if (multipartData != null && param.isAnnotationPresent(com.monframework.annotation.UploadedFile.class)) {
                com.monframework.annotation.UploadedFile uploadAnnotation = param
                        .getAnnotation(com.monframework.annotation.UploadedFile.class);
                String fileParamName = uploadAnnotation.value().isEmpty() ? paramName : uploadAnnotation.value();

                System.out.println("  -> Annotation @UploadedFile détectée pour: " + fileParamName);

                if (multipartData.containsKey(fileParamName)) {
                    Object fileInfo = multipartData.get(fileParamName);

                    if (fileInfo instanceof Map) {
                        Map<String, Object> fileMap = (Map<String, Object>) fileInfo;

                        // Gestion selon le type attendu
                        if (paramType == byte[].class) {
                            args[i] = fileMap.get("bytes");
                            System.out.println("  -> Fichier injecté comme byte[]: " +
                                    ((byte[]) args[i]).length + " bytes");
                        } else if (paramType == Byte[].class) {
                            byte[] primitiveBytes = (byte[]) fileMap.get("bytes");
                            Byte[] objectBytes = new Byte[primitiveBytes.length];
                            for (int j = 0; j < primitiveBytes.length; j++) {
                                objectBytes[j] = primitiveBytes[j];
                            }
                            args[i] = objectBytes;
                            System.out.println("  -> Fichier injecté comme Byte[]: " +
                                    objectBytes.length + " bytes");
                        } else if (paramType == Map.class) {
                            args[i] = fileMap;
                            System.out.println("  -> Fichier injecté comme Map: " + fileMap.keySet());
                        } else if (paramType == List.class || paramType == ArrayList.class) {
                            // Pour les fichiers multiples
                            List<byte[]> fileList = new ArrayList<>();
                            if (fileMap.containsKey("bytes")) {
                                fileList.add((byte[]) fileMap.get("bytes"));
                                System.out.println("  -> Fichier injecté comme List<byte[]>: 1 fichier");
                            } else if (fileMap.containsKey("files")) {
                                // Pour plusieurs fichiers avec le même nom
                                List<Map<String, Object>> files = (List<Map<String, Object>>) fileMap.get("files");
                                for (Map<String, Object> file : files) {
                                    fileList.add((byte[]) file.get("bytes"));
                                }
                                System.out.println("  -> Fichiers injectés comme List<byte[]>: " +
                                        fileList.size() + " fichiers");
                            }
                            args[i] = fileList;
                        }
                    }
                } else {
                    System.out.println("  -> Fichier non trouvé pour le paramètre: " + fileParamName);
                    args[i] = null;
                }
                continue;
            }

            // SPRINT 8: Si c'est un Map<String, Object> ou Map pour toutes les données
            if (Map.class.isAssignableFrom(paramType)) {
                System.out.println("  -> Détection Map: injection de toutes les données");
                Map<String, Object> allData = new HashMap<>();

                System.out.println("  -> Nombre de paramètres reçus: " + requestParams.size());

                for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                    String paramKey = entry.getKey();
                    String[] values = entry.getValue();

                    if (values == null || values.length == 0) {
                        continue;
                    }

                    System.out.println("  -> Paramètre " + paramKey + " = " +
                            (values.length == 1 ? values[0] : Arrays.toString(values)));

                    if (values.length == 1) {
                        allData.put(paramKey, values[0]);
                    } else {
                        allData.put(paramKey, values);
                    }
                }

                // Ajouter les paramètres du chemin
                if (pathVariables != null) {
                    allData.putAll(pathVariables);
                }

                // SPRINT 10: Ajouter les fichiers multipart si présents
                if (multipartData != null) {
                    allData.putAll(multipartData);
                    System.out.println("  -> Ajout des données multipart à la Map");
                }

                args[i] = allData;
                System.out.println("  -> Map créée avec " + allData.size() + " entrées");
                continue;
            }

            // SPRINT 8bis: Si c'est un objet complexe
            if (isComplexObject(param, paramType)) {
                System.out.println("  -> Détection objet complexe: " + paramType.getSimpleName());
                try {
                    // D'abord créer l'objet
                    Object obj = paramType.newInstance();

                    // Déterminer si on doit utiliser un préfixe
                    boolean usePrefix = shouldUsePrefix(paramName, paramPatterns);

                    System.out.println("  -> Utilisation préfixe pour " + paramName + ": " + usePrefix);

                    // Lier les paramètres à l'objet
                    bindObjectToParameters(obj, paramName, requestParams, usePrefix);

                    args[i] = obj;
                    System.out.println("  -> Objet " + paramType.getSimpleName() + " créé et rempli");

                } catch (Exception e) {
                    System.out.println("  -> Erreur lors de la création de l'objet: " + e.getMessage());
                    e.printStackTrace();
                    args[i] = null;
                }
                continue;
            }

            // Gestion des annotations existantes (@RequestParam, @PathVariable)
            if (param.isAnnotationPresent(PathVariable.class)) {
                PathVariable pathAnnotation = param.getAnnotation(PathVariable.class);
                String variableName = pathAnnotation.value();
                String stringValue = pathVariables.get(variableName);

                // Convertir la valeur selon le type du paramètre
                args[i] = convertValue(stringValue, param.getType());
                System.out.println("    @PathVariable " + variableName + " = " + args[i]);
            } else if (param.isAnnotationPresent(RequestParam.class)) {
                // Gestion RequestParam (sprint 6bis)
                RequestParam requestAnnotation = param.getAnnotation(RequestParam.class);
                String defaultValue = requestAnnotation.defaultValue();

                // SPRINT 10: Chercher dans les paramètres texte d'abord
                String stringValue = null;
                if (requestParams.containsKey(paramName) && requestParams.get(paramName).length > 0) {
                    stringValue = requestParams.get(paramName)[0];
                } else if (multipartData != null && multipartData.containsKey(paramName)) {
                    // SPRINT 10: Si c'est dans les données multipart (champ texte)
                    Object value = multipartData.get(paramName);
                    if (value instanceof String) {
                        stringValue = (String) value;
                    }
                }

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
                else if (requestParams.containsKey(paramName) && requestParams.get(paramName).length > 0) {
                    stringValue = requestParams.get(paramName)[0];
                    System.out.println("     Trouvé dans query parameters: " + stringValue);
                }
                // 3. SPRINT 10: Chercher dans les données multipart (champs texte)
                else if (multipartData != null && multipartData.containsKey(paramName)) {
                    Object value = multipartData.get(paramName);
                    if (value instanceof String) {
                        stringValue = (String) value;
                        System.out.println("     Trouvé dans multipart data: " + stringValue);
                    }
                }
                // 4. Si pas trouvé, essayer avec le nom de la variable dans l'URL
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

    // SPRINT 10: Méthode pour extraire les paramètres texte des données multipart
    private Map<String, String[]> extractTextParametersFromMultipart(Map<String, Object> multipartData) {
        Map<String, String[]> textParams = new HashMap<>();

        for (Map.Entry<String, Object> entry : multipartData.entrySet()) {
            if (entry.getValue() instanceof String) {
                // C'est un champ texte
                textParams.put(entry.getKey(), new String[] { (String) entry.getValue() });
            } else if (entry.getValue() instanceof String[]) {
                // Tableau de strings
                textParams.put(entry.getKey(), (String[]) entry.getValue());
            }
            // Les fichiers (Map) sont ignorés ici
        }

        return textParams;
    }

    // Analyse les patterns de paramètres pour déterminer si on doit utiliser un
    // préfixe
    private Map<String, List<String>> analyzeParameterPatterns(Map<String, String[]> requestParams) {
        Map<String, List<String>> patterns = new HashMap<>();

        for (String paramName : requestParams.keySet()) {
            if (paramName.contains(".")) {
                String prefix = paramName.substring(0, paramName.indexOf('.'));
                if (!patterns.containsKey(prefix)) {
                    patterns.put(prefix, new java.util.ArrayList<>());
                }
                patterns.get(prefix).add(paramName);
            }
        }

        return patterns;
    }

    // Détermine si on doit utiliser un préfixe pour ce paramètre
    private boolean shouldUsePrefix(String paramName, Map<String, List<String>> paramPatterns) {
        // Si le paramètre existe dans les patterns, on utilise le préfixe
        return paramPatterns.containsKey(paramName);
    }

    // Lie les paramètres à un objet
    private void bindObjectToParameters(Object obj, String paramName, Map<String, String[]> requestParams,
            boolean usePrefix) {
        Class<?> clazz = obj.getClass();

        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values == null || values.length == 0) {
                continue;
            }

            String propertyPath = key;

            // Si on doit utiliser un préfixe
            if (usePrefix) {
                // Vérifier si le paramètre commence par le nom du paramètre
                if (key.startsWith(paramName + ".")) {
                    propertyPath = key.substring(paramName.length() + 1);
                } else {
                    // Ce paramètre ne concerne pas cet objet
                    continue;
                }
            } else {
                // Si pas de préfixe, on prend les paramètres sans point
                // OU les paramètres qui commencent par un mot connu (comme "department.")
                if (key.contains(".")) {
                    // C'est peut-être une propriété imbriquée
                    String firstPart = key.substring(0, key.indexOf('.'));
                    // Vérifier si cette première partie est un attribut de l'objet
                    if (hasProperty(clazz, firstPart)) {
                        propertyPath = key;
                    } else {
                        continue;
                    }
                }
            }

            // Setter la propriété
            try {
                setPropertyOnObject(obj, propertyPath, values);
            } catch (Exception e) {
                System.out.println("  -> Erreur lors du binding de " + propertyPath + ": " + e.getMessage());
            }
        }
    }

    // Vérifie si une classe a une propriété (par son nom)
    private boolean hasProperty(Class<?> clazz, String propertyName) {
        String getterName = "get" + capitalize(propertyName);
        String setterPrefix = "set" + capitalize(propertyName);

        // Chercher un getter
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(getterName) && method.getParameterCount() == 0) {
                return true;
            }
            if (method.getName().startsWith(setterPrefix) && method.getParameterCount() == 1) {
                return true;
            }
        }

        // Chercher un champ
        try {
            clazz.getDeclaredField(propertyName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private boolean isComplexObject(Parameter param, Class<?> type) {
        // Si c'est annoté avec @RequestParam ou @PathVariable, ce n'est pas un objet
        // complexe
        if (param.isAnnotationPresent(PathVariable.class) ||
                param.isAnnotationPresent(RequestParam.class)) {
            return false;
        }

        // Si c'est un type simple, ce n'est pas un objet complexe
        if (isSimpleType(type)) {
            return false;
        }

        // Si c'est un tableau, ce n'est pas un objet complexe
        if (type.isArray()) {
            return false;
        }

        // Si c'est un Map, déjà traité séparément
        if (Map.class.isAssignableFrom(type)) {
            return false;
        }

        // SPRINT 10: Si c'est annoté avec @UploadedFile, ce n'est pas un objet complexe
        if (param.isAnnotationPresent(com.monframework.annotation.UploadedFile.class)) {
            return false;
        }

        // Sinon, c'est un objet complexe
        return true;
    }

    private boolean isSimpleType(Class<?> type) {
        return type == String.class ||
                type == Integer.class || type == int.class ||
                type == Long.class || type == long.class ||
                type == Double.class || type == double.class ||
                type == Boolean.class || type == boolean.class ||
                type == Float.class || type == float.class ||
                type == Byte.class || type == byte.class || // Ajoutez cette ligne
                type.isEnum() ||
                type.isArray() && isSimpleType(type.getComponentType());
    }

    // Méthode pour setter une propriété sur un objet (avec support pour les
    // propriétés imbriquées)
    private void setPropertyOnObject(Object obj, String propertyPath, String[] values) {
        try {
            // Si le chemin contient un point, c'est une propriété imbriquée
            if (propertyPath.contains(".")) {
                String[] parts = propertyPath.split("\\.", 2);
                String propertyName = parts[0];
                String nestedPath = parts[1];

                // Récupérer ou créer l'objet imbriqué
                Object nestedObject = getOrCreateNestedObject(obj, propertyName);
                if (nestedObject != null) {
                    setPropertyOnObject(nestedObject, nestedPath, values);
                } else {
                    System.out.println("    Cannot create nested object for: " + propertyName);
                }
            } else {
                // Propriété simple
                setSimpleProperty(obj, propertyPath, values);
            }
        } catch (Exception e) {
            System.out.println("  Error setting property " + propertyPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer ou créer un objet imbriqué
    private Object getOrCreateNestedObject(Object parent, String propertyName) throws Exception {
        Class<?> parentClass = parent.getClass();

        // Chercher un getter
        String getterName = "get" + capitalize(propertyName);
        Method getter = null;

        try {
            getter = parentClass.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            // Essayer avec "is" pour les boolean
            getterName = "is" + capitalize(propertyName);
            try {
                getter = parentClass.getMethod(getterName);
            } catch (NoSuchMethodException e2) {
                System.out.println("    No getter found for: " + propertyName);
                // Essayer d'accéder directement au champ
                try {
                    java.lang.reflect.Field field = parentClass.getDeclaredField(propertyName);
                    field.setAccessible(true);
                    Object nested = field.get(parent);
                    if (nested == null) {
                        // Déterminer le type du champ
                        Class<?> fieldType = field.getType();
                        if (!fieldType.isPrimitive() && fieldType != String.class) {
                            nested = fieldType.newInstance();
                            field.set(parent, nested);
                            System.out.println("    Created new " + fieldType.getSimpleName()
                                    + " via field access for: " + propertyName);
                        }
                    }
                    return nested;
                } catch (NoSuchFieldException e3) {
                    System.out.println("    No field found for: " + propertyName);
                    return null;
                }
            }
        }

        if (getter != null) {
            // Récupérer l'objet existant
            Object nested = getter.invoke(parent);
            if (nested == null) {
                // Créer une nouvelle instance
                Class<?> nestedType = getter.getReturnType();
                try {
                    nested = nestedType.newInstance();

                    // Chercher le setter
                    String setterName = "set" + capitalize(propertyName);
                    try {
                        Method setter = parentClass.getMethod(setterName, nestedType);
                        setter.invoke(parent, nested);
                        System.out.println(
                                "    Created new " + nestedType.getSimpleName() + " via setter for: " + propertyName);
                    } catch (NoSuchMethodException e) {
                        System.out.println("    No setter found for: " + propertyName);
                        // Essayer d'accéder directement au champ
                        try {
                            java.lang.reflect.Field field = parentClass.getDeclaredField(propertyName);
                            field.setAccessible(true);
                            field.set(parent, nested);
                            System.out.println("    Set field directly for: " + propertyName);
                        } catch (NoSuchFieldException e2) {
                            System.out.println("    Cannot set field for: " + propertyName);
                        }
                    }
                } catch (InstantiationException e) {
                    System.out.println("    Cannot instantiate " + nestedType.getSimpleName() + ": " + e.getMessage());
                    return null;
                }
            }
            return nested;
        }

        return null;
    }

    // Méthode pour setter une propriété simple
    private void setSimpleProperty(Object obj, String propertyName, String[] values) {
        try {
            Class<?> clazz = obj.getClass();

            // Chercher le setter
            String setterName = "set" + capitalize(propertyName);

            // D'abord essayer de trouver un setter pour un tableau
            if (values.length > 1 || (values.length == 1 && values[0].contains(","))) {
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];

                        if (paramType.isArray() && paramType.getComponentType() == String.class) {
                            // Tableau de String
                            method.invoke(obj, (Object) values);
                            System.out.println(
                                    "    Set array property: " + propertyName + " = " + Arrays.toString(values));
                            return;
                        }
                    }
                }
            }

            // Chercher un setter normal
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];

                    if (values.length > 0) {
                        // Valeur simple
                        Object convertedValue = convertValue(values[0], paramType);
                        method.invoke(obj, convertedValue);
                        System.out.println("    Set simple property: " + propertyName + " = " + convertedValue
                                + " (type: " + paramType.getSimpleName() + ")");
                        return;
                    }
                }
            }

            // Si pas de setter, essayer d'accéder au champ directement
            try {
                java.lang.reflect.Field field = clazz.getDeclaredField(propertyName);
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                if (values.length > 0) {
                    Object convertedValue = convertValue(values[0], fieldType);
                    field.set(obj, convertedValue);
                    System.out.println("    Set field directly: " + propertyName + " = " + convertedValue);
                }
            } catch (NoSuchFieldException e) {
                System.out.println("    No setter or field found for: " + propertyName);
            }

        } catch (Exception e) {
            System.out.println("    Error in setSimpleProperty for " + propertyName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

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
                url.equals("/formulaire.html") ||
                url.equals("/api-test.html") ||
                url.equals("/upload-test.html");
    }

    private void serveStaticResource(String resourcePath, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Ressource statique: " + resourcePath);

        // Déléguer au servlet par défaut de Tomcat
        getServletContext().getNamedDispatcher("default").forward(request, response);
    }

    private void handleMethodResult(Object result, PrintWriter out, Method method,
            HttpServletResponse response) {

        // Si c'est une méthode JSON, traiter différemment
        if (isJsonResponseMethod(method)) {
            try {
                handleJsonResponse(result, method, null, response);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Sinon, traitement normal
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

        System.out.println("Requête " + httpMethod + " reçue: " + requestedUrl);

        Map<String, Object> multipartData = null;
        if (isMultipartRequest(request)) {
            try {
                multipartData = processMultipartData(request);
                System.out.println("Requête multipart traitée, " + multipartData.size() + " éléments");
            } catch (Exception e) {
                System.out.println("Erreur lors du traitement multipart: " + e.getMessage());
                e.printStackTrace();
            }
        }
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
                Object[] args = prepareArguments(routeInfo, requestedUrl, request, multipartData);

                // Exécuter la méthode
                Object result = method.invoke(controllerInstance, args);

                // Gérer le retour
                if (result instanceof ModelView) {
                    handleModelView((ModelView) result, request, response);
                } else {
                    handleMethodResult(result, out, method, response);
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

    // Méthode pour vérifier si une méthode est annotée avec @JsonResponse
    private boolean isJsonResponseMethod(Method method) {
        return method.isAnnotationPresent(JsonAPI.class) ||
                method.getDeclaringClass().isAnnotationPresent(RestController.class);
    }

    // Méthode pour vérifier si une classe est un RestController
    private boolean isRestController(Class<?> clazz) {
        return clazz.isAnnotationPresent(RestController.class);
    }

    // Méthode pour traiter une réponse JSON
    private void handleJsonResponse(Object result, Method method,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Récupérer l'annotation @JsonAPI si présente
        JsonAPI jsonAnnotation = method.getAnnotation(JsonAPI.class);
        int statusCode = 200;
        String customMessage = "";

        // DÉTERMINER LE CODE DE STATUT EN FONCTION DU RÉSULTAT
        // 1. Vérifier d'abord si c'est un JsonResponse et utiliser son code
        if (result instanceof com.monframework.JsonResponse) {
            com.monframework.JsonResponse jsonResponse = (com.monframework.JsonResponse) result;
            statusCode = jsonResponse.getCode();
        }

        // 2. L'annotation @JsonAPI peut écraser le code si elle spécifie explicitement
        // un code
        if (jsonAnnotation != null) {
            // Si l'annotation a un code différent de 200, l'utiliser
            if (jsonAnnotation.statusCode() != 200) {
                statusCode = jsonAnnotation.statusCode();
            }
            customMessage = jsonAnnotation.message();
            if (jsonAnnotation.contentType() != null && !jsonAnnotation.contentType().isEmpty()) {
                response.setContentType(jsonAnnotation.contentType() + "; charset=UTF-8");
            }
        }

        response.setStatus(statusCode); // Définir le code HTTP correct

        // Préparer la réponse
        Object jsonResult;

        if (result instanceof com.monframework.JsonResponse) {
            // Si c'est déjà un JsonResponse, l'utiliser tel quel
            jsonResult = result;
        } else if (result instanceof String && ((String) result).startsWith("{")) {
            // Si c'est déjà une chaîne JSON, la retourner directement
            response.getWriter().write((String) result);
            return;
        } else {
            // Sinon, encapsuler dans un JsonResponse standard
            com.monframework.JsonResponse jsonResponse;

            if (result instanceof List) {
                List<?> list = (List<?>) result;
                jsonResponse = com.monframework.JsonResponse.withCount(list, list.size());
            } else if (result instanceof Object[]) {
                Object[] array = (Object[]) result;
                jsonResponse = com.monframework.JsonResponse.withCount(array, array.length);
            } else {
                jsonResponse = com.monframework.JsonResponse.success(result);
            }

            // Si l'annotation a un message, l'utiliser
            if (!customMessage.isEmpty()) {
                jsonResponse.setMessage(customMessage);
            }

            // Si le résultat n'était pas un JsonResponse mais que l'annotation a un code,
            // utiliser ce code pour le JsonResponse créé
            if (jsonAnnotation != null && jsonAnnotation.statusCode() != 200) {
                jsonResponse.setCode(jsonAnnotation.statusCode());
            }

            jsonResult = jsonResponse;
        }

        // Convertir en JSON
        String json = JsonUtil.toJson(jsonResult);
        response.getWriter().write(json);

        System.out.println("Réponse JSON envoyée (status " + statusCode + "): " +
                json.substring(0, Math.min(200, json.length())) + "...");
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().toLowerCase().startsWith("multipart/form-data");
    }

    private Map<String, Object> processMultipartData(HttpServletRequest request)
            throws IOException, ServletException {

        Map<String, Object> multipartData = new HashMap<>();

        // Récupérer toutes les parties
        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            String name = part.getName();

            if (part.getContentType() != null) {
                // C'est un fichier
                InputStream is = part.getInputStream();
                byte[] fileBytes = readAllBytes(is);

                // Stocker les métadonnées du fichier
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("bytes", fileBytes);
                fileInfo.put("filename", getFileName(part));
                fileInfo.put("contentType", part.getContentType());
                fileInfo.put("size", part.getSize());

                multipartData.put(name, fileInfo);
            } else {
                // C'est un champ texte
                String value = readPartAsString(part);
                multipartData.put(name, value);
            }
        }

        return multipartData;
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private String readPartAsString(Part part) throws IOException {
        InputStream is = part.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}