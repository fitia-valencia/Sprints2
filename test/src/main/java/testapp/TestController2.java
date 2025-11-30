package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller(url = "/admin")
public class TestController2 {
    
    @Route(url = "/dashboard")
    public String adminDashboard() {
        return "Tableau de bord administrateur";
    }
}