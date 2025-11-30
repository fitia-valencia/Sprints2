package testapp;

import com.monframework.annotation.Route;

public class TestController {
    
    @Route("/hello")
    public String sayHello() {
        return "Bonjour! URL /hello capturée!";
    }
    
    @Route("/test")
    public String testMethod() {
        return "Test réussi!";
    }
}