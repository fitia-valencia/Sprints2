package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller(url = "/products")
public class ProductController {
    
    @Route(url = "/list")
    public String listProducts() {
        return "Liste des produits";
    }
    
    @Route(url = "/details")
    public String productDetails() {
        return "Détails du produit";
    }
    
    public String notAnnotatedMethod() {
        return "Cette méthode ne sera pas exposée";
    }
}