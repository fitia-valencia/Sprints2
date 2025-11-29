package testapp;

import com.monframework.annotation.Route;
import com.monframework.scanner.RouteScanner;

import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

public class FrontController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String requestedUrl = request.getRequestURI().substring(request.getContextPath().length());
        
        try {
            // Scanner les méthodes annotées
            Set<Method> methods = RouteScanner.findAnnotatedMethods("testapp");
            
            // Chercher la méthode correspondante à l'URL
            for (Method method : methods) {
                Route route = method.getAnnotation(Route.class);
                if (route.url().equals(requestedUrl)) {
                    // Exécuter la méthode
                    Object instance = method.getDeclaringClass().newInstance();
                    Object result = method.invoke(instance);
                    
                    response.getWriter().println("URL trouvée: " + requestedUrl);
                    response.getWriter().println("Méthode exécutée: " + method.getName());
                    response.getWriter().println("Résultat: " + result);
                    return;
                }
            }
            
            // Si aucune méthode trouvée
            response.getWriter().println("Aucune méthode trouvée pour l'URL: " + requestedUrl);
            
        } catch (Exception e) {
            response.getWriter().println("Erreur: " + e.getMessage());
        }
    }
}