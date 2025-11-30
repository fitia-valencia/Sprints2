package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller(url="/auth")
public class AuthController {
    
    @Route(url="/login")
    public String login() {
        return "Page de connexion";
    }
    
    @Route(url="/logout")
    public String logout() {
        return "Déconnexion";
    }
}