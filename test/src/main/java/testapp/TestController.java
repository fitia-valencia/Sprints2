package testapp;

import com.monframework.annotation.Route;

public class TestController {
    
    @Route(url = "/hello")
    public String sayHello() {
        return "Bonjour! URL /hello capturée!";
    }
    
    @Route(url = "/test")
    public String testMethod() {
        return "Test réussi!";
    }
}