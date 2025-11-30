package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller("/auth")
public class AuthController {
    
    @Route("/login")
    public String login() {
        return "Page de connexion";
    }
    
    @Route("/logout")
    public String logout() {
        return "Déconnexion";
    }
}