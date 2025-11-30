package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller("/admin")
public class TestController2 {
    
    @Route("/dashboard")
    public String adminDashboard() {
        return "Tableau de bord administrateur";
    }
}