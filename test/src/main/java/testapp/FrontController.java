package testapp;

import com.monframework.scanner.ControllerScanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class FrontController extends HttpServlet {
    
    private ControllerScanner scanner;
    
    @Override
    public void init() throws ServletException {
        scanner = new ControllerScanner();
        scanner.scanControllers("testapp");
        System.out.println("=== Routes initialisées ===");
        System.out.println(scanner.getRouteMap());
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
        }
    }
}