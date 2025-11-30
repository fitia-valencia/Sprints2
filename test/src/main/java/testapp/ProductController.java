package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller("/products")
public class ProductController {
    
    @Route("/list")
    public String listProducts() {
        return "Liste des produits";
    }
    
    @Route("/details")
    public String productDetails() {
        return "Détails du produit";
    }
    
    public String notAnnotatedMethod() {
        return "Cette méthode ne sera pas exposée";
    }
}