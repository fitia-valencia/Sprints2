package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.PostMapping;
import com.monframework.annotation.GetMapping;
import com.monframework.annotation.AllowAnonymous;
import com.monframework.JsonResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

@Controller
public class AuthController {

    @AllowAnonymous
    @PostMapping("/login")
    public JsonResponse login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("DEBUG - Login attempt: " + username);

        // Simulation d'authentification
        if ("admin".equals(username) && "admin123".equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("authenticated", true);
            session.setAttribute("user", username);
            session.setAttribute("roles", Arrays.asList("admin", "user"));

            System.out.println("DEBUG - Admin logged in successfully");

            return JsonResponse.success("Connexion réussie",
                    Map.of("username", username, "roles", Arrays.asList("admin", "user")));
        } else if ("user".equals(username) && "user123".equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("authenticated", true);
            session.setAttribute("user", username);
            session.setAttribute("roles", Arrays.asList("user"));

            System.out.println("DEBUG - User logged in successfully");

            return JsonResponse.success("Connexion réussie",
                    Map.of("username", username, "roles", Arrays.asList("user")));
        }

        System.out.println("DEBUG - Login failed for: " + username);
        return JsonResponse.error(401, "Identifiants incorrects");
    }

    @GetMapping("/logout")
    public JsonResponse logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
            System.out.println("DEBUG - Session invalidated");
        }
        return JsonResponse.success("Déconnexion réussie");
    }

    @AllowAnonymous
    @GetMapping("/public/info")
    public JsonResponse publicInfo() {
        return JsonResponse.success("Cette information est publique");
    }

    // Méthode pour tester l'injection
    @AllowAnonymous
    @GetMapping("/test/injection")
    public String testInjection(HttpServletRequest request, HttpSession session) {
        return "Request: " + request.getMethod() +
                ", Session ID: " + (session != null ? session.getId() : "null");
    }
}