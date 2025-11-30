package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller(url = "/api")
public class TestController1 {
    
    @Route(url = "/hello")
    public String sayHello() {
        return "Bonjour depuis TestController1! : chaine de caractères retournée.";
    }
    
    @Route(url = "/users")
    public String getUsers() {
        return "Liste des utilisateurs";
    }

    @Route(url ="/html")
    public String getHTML() {
        return "<h2>Contenu HTML</h2><p>Ceci est du <strong>HTML</strong> généré dynamiquement.</p>";
    }

    @Route(url="/number")
    public Integer getNumber() {
        return 42;
    }

    @Route(url="/boolean")
    public Boolean getBoolean() {
        return true;
    }
}