package testapp;

import com.monframework.scanner.ControllerScanner;
import com.monframework.ModelView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
        
        if (scanner.urlExists(requestedUrl)) {
            try {
                Method method = scanner.getMethodForUrl(requestedUrl);
                Class<?> controllerClass = method.getDeclaringClass();
                Object controllerInstance = controllerClass.newInstance();                
                System.out.println(" Exécution: " + controllerClass.getSimpleName() + "." + method.getName());
                
                Object result = method.invoke(controllerInstance);
                if (result instanceof ModelView) {
                    handleModelView((ModelView) result, request, response);
                } else {
                    handleMethodResult(result, out, method);
                }
                
            } catch (Exception e) {
                System.out.println(" ERREUR lors de l'exécution: " + e.getMessage());
                e.printStackTrace();
                
                out.println("<h1> Erreur d'exécution</h1>");
                out.println("<pre style='color: red;'>" + e.getMessage() + "</pre>");
            }
        } else {
            System.out.println(" URL non trouvée: " + requestedUrl);
            out.println("<h1>404 - URL non trouvée</h1>");
            out.println("<p>Aucune méthode trouvée pour: " + requestedUrl + "</p>");
        }
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