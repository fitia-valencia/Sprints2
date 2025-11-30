package testapp;

import com.monframework.scanner.ControllerScanner;
import com.monframework.scanner.RouteInfo;
import com.monframework.ModelView;
import com.monframework.annotation.PathVariable;

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
    
    private void displayAllRoutes() {
        System.out.println("\n LISTE DES ROUTES DISPONIBLES:");
        System.out.println("=================================");
        
        Map<String, Method> routeMap = scanner.getRouteMap();
        
        if (routeMap.isEmpty()) {
            System.out.println("Aucune route trouvée!");
            return;
        }
        
        for (Map.Entry<String, Method> entry : routeMap.entrySet()) {
            String url = entry.getKey();
            Method method = entry.getValue();
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            
            System.out.println(" " + url + " -> " + className + "." + methodName + "()");
        }
        
        System.out.println("Total: " + routeMap.size() + " route(s) configurée(s)");
        System.out.println("=================================\n");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String requestedUrl = request.getRequestURI().substring(request.getContextPath().length());
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        System.out.println(" Requête reçue: " + requestedUrl);
        
        RouteInfo routeInfo = scanner.findMatchingRoute(requestedUrl);
        
        if (routeInfo != null) {
            try {
                Method method = routeInfo.getMethod();
                Object controllerInstance = method.getDeclaringClass().newInstance();
                
                System.out.println("Exécution: " + method.getDeclaringClass().getSimpleName() + "." + method.getName());
                
                // Préparer les arguments pour la méthode
                Object[] args = prepareArguments(routeInfo, requestedUrl);
                
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
    
    private Object[] prepareArguments(RouteInfo routeInfo, String requestedUrl) {
        Parameter[] parameters = routeInfo.getParameters();
        Object[] args = new Object[parameters.length];
        
        // Extraire les variables du path
        Map<String, String> pathVariables = routeInfo.extractPathVariablesValues(requestedUrl);
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            
            // Vérifier si c'est un PathVariable
            if (param.isAnnotationPresent(PathVariable.class)) {
                PathVariable pathAnnotation = param.getAnnotation(PathVariable.class);
                String variableName = pathAnnotation.value();
                String stringValue = pathVariables.get(variableName);
                
                // Convertir la valeur selon le type du paramètre
                args[i] = convertValue(stringValue, param.getType());
                System.out.println("    @PathVariable " + variableName + " = " + args[i]);
            } else {
                // Pour les autres paramètres (seront gérés dans les sprints suivants)
                args[i] = null;
            }
        }
        return args;
    }

    private Object convertValue(String stringValue, Class<?> targetType) {
        if (stringValue == null) return null;
        
        if (targetType == String.class) {
            return stringValue;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(stringValue);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(stringValue);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(stringValue);
        }
        // Ajouter d'autres types au besoin
        return stringValue;
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
    
    private void handleMethodResult(Object result, PrintWriter out, Method method) {
        System.out.println("Affichage direct du résultat...");
        
        out.println("<html><head><title>Résultat</title></head><body>");
        out.println("<h1>Méthode exécutée avec succès</h1>");
        out.println("<p><strong>Méthode:</strong> " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()</p>");
        
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
}