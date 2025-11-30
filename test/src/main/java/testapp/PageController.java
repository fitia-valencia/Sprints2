package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

import testapp.models.Product;

import com.monframework.ModelView;

@Controller("/pages")
public class PageController {
    
    @Route("/home")
    public ModelView homePage() {
        ModelView mv = new ModelView("home.jsp");
        mv.addObject("title", "Page d'accueil");
        mv.addObject("message", "Bienvenue sur notre site!");
        mv.addObject("users", new String[]{"Alice", "Bob", "Charlie"});
        return mv;
    }
    
    @Route("/profile")
    public ModelView userProfile() {
        ModelView mv = new ModelView("profile.jsp");
        mv.addObject("name", "John Doe");
        mv.addObject("email", "john@example.com");
        mv.addObject("age", 30);
        return mv;
    }
    
    @Route("/products")
    public ModelView productList() {
        ModelView mv = new ModelView("products.jsp");
        mv.addObject("products", java.util.List.of(
            new Product("Laptop", 1200.0),
            new Product("Phone", 800.0),
            new Product("Tablet", 500.0)
        ));
        return mv;
    }

}