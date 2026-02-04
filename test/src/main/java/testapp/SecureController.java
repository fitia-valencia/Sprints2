package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.GetMapping;
import com.monframework.annotation.Authenticated;
import com.monframework.annotation.Role;
import com.monframework.annotation.Roles;
import com.monframework.JsonResponse;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class SecureController {
    
    @Authenticated
    @GetMapping("/secure/profile")
    public JsonResponse getProfile(HttpSession session) {
        String username = (String) session.getAttribute("user");
        System.out.println("DEBUG - Getting profile for: " + username);
        
        return JsonResponse.success("Profil de " + username, 
            Map.of("username", username, 
                   "sessionId", session.getId(),
                   "authenticated", session.getAttribute("authenticated"),
                   "roles", session.getAttribute("roles")));
    }
    
    @Role("user")
    @GetMapping("/secure/user-data")
    public JsonResponse getUserData() {
        return JsonResponse.success("Données utilisateur");
    }
    
    @Role("admin")
    @GetMapping("/secure/admin-data")
    public JsonResponse getAdminData() {
        return JsonResponse.success("Données administrateur");
    }
    
    @Roles({"admin", "moderator"})
    @GetMapping("/secure/moderate")
    public JsonResponse moderateContent() {
        return JsonResponse.success("Contenu modéré");
    }
    
    @Roles(value = {"admin", "manager"}, requireAll = true)
    @GetMapping("/secure/manage")
    public JsonResponse manageSystem() {
        return JsonResponse.success("Gestion système");
    }
}