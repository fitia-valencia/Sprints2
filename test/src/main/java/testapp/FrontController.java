package testapp;

import com.monframework.scanner.ControllerScanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

public class FrontController extends HttpServlet {
    
    private ControllerScanner scanner;
    
    @Override
    public void init() throws ServletException {
        System.out.println("=== INITIALISATION DE L'APPLICATION ===");
        
        scanner = new ControllerScanner();
        scanner.scanControllers("testapp");
        
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
        
        if (scanner.urlExists(requestedUrl)) {
            try {
                Method method = scanner.getMethodForUrl(requestedUrl);
                Class<?> controllerClass = method.getDeclaringClass();
                Object controllerInstance = controllerClass.newInstance();                
                System.out.println(" Exécution: " + controllerClass.getSimpleName() + "." + method.getName());
                
                Object result = method.invoke(controllerInstance);
                handleMethodResult(result, out, method);
                
            } catch (Exception e) {
                response.getWriter().println("<h1>Erreur d'exécution</h1>");
                response.getWriter().println("<p>" + e.getMessage() + "</p>");
            }
        } else {
            response.getWriter().println("<h1>404 - URL non trouvée</h1>");
            response.getWriter().println("<p>Aucune méthode trouvée pour: " + requestedUrl + "</p>");
            response.getWriter().println("<h3>Routes disponibles:</h3>");
            response.getWriter().println("<ul>");
            for (String url : scanner.getRouteMap().keySet()) {
                response.getWriter().println("<li>" + url + "</li>");
            }
            response.getWriter().println("</ul>");
        }
    }

    private void handleMethodResult(Object result, PrintWriter out, Method method) {
        out.println("<html><head><title>Résultat</title></head><body>");
        out.println("<h1>Méthode exécutée avec succès</h1>");
        out.println("<p><strong>Méthode:</strong> " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()</p>");
        
        if (result != null) {
            out.println("<p><strong>Type de retour:</strong> " + result.getClass().getSimpleName() + "</p>");
            out.println("<div style='background: #f5f5f5; padding: 15px; border-radius: 5px;'>");
            out.println("<strong>Résultat:</strong><br>");
            
            if (result instanceof String) {
                out.println("<pre>" + result + "</pre>");
            } else {
                out.println("<pre>" + result.toString() + "</pre>");
            }
            
            out.println("</div>");
        } else {
            out.println("<p><em>La méthode a retourné null</em></p>");
        }
        
        out.println("<br><a href='/testapp'> Retour à l'accueil</a>");
        out.println("</body></html>");
    }
}