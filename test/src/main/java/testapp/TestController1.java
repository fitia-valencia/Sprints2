package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller(url = "/api")
public class TestController1 {
    
    @Route(url = "/hello")
    public String sayHello() {
        return "Bonjour depuis TestController1!";
    }
    
    @Route(url = "/users")
    public String getUsers() {
        return "Liste des utilisateurs";
    }
}