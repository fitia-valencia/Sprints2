package testapp;

import com.monframework.scanner.ControllerScanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        
        response.setContentType("text/html");
        
        if (scanner.urlExists(requestedUrl)) {
            try {
                Method method = scanner.getMethodForUrl(requestedUrl);
                Object instance = method.getDeclaringClass().newInstance();
                Object result = method.invoke(instance);
                
                response.getWriter().println("<h1>URL trouvée: " + requestedUrl + "</h1>");
                response.getWriter().println("<p>Méthode: " + method.getName() + "</p>");
                response.getWriter().println("<p>Résultat: " + result + "</p>");
                
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
}