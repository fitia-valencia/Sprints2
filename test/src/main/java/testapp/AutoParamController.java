package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;
import com.monframework.ModelView;

@Controller("/auto")
public class AutoParamController {

    // 🆕 Sprint 6ter: Injection automatique par nom de paramètre
    // URL: /auto/user/123 → id=123, name=null
    // URL: /auto/user/123?name=Alice → id=123, name=Alice
    @Route("/user/{id}")
    public ModelView getUserById(int id, String name) {
        ModelView mv = new ModelView("userAuto.jsp");
        mv.addObject("id", id);
        mv.addObject("name", name != null ? name : "Utilisateur " + id);
        mv.addObject("method", "Injection automatique");
        return mv;
    }

    // URL: /auto/search?query=java&page=2 → query=java, page=2
    @Route("/search")
    public ModelView search(String query, int page) {
        ModelView mv = new ModelView("searchAuto.jsp");
        mv.addObject("query", query);
        mv.addObject("page", page);
        mv.addObject("results", "Résultats pour: '" + query + "' - Page " + page);
        mv.addObject("method", "Injection automatique");
        return mv;
    }

    // URL: /auto/product/123/category/electronics → productId=123, category=electronics
    @Route("/product/{productId}/category/{category}")
    public ModelView getProduct(int productId, String category) {
        ModelView mv = new ModelView("productAuto.jsp");
        mv.addObject("productId", productId);
        mv.addObject("category", category);
        mv.addObject("productName", "Produit " + productId + " - " + category);
        mv.addObject("method", "Injection automatique");
        return mv;
    }

    // URL: /auto/mixed/123/details?format=json&limit=10 → id=123, format=json, limit=10
    @Route("/mixed/{id}/details")
    public ModelView mixedParams(int id, String format, int limit) {
        ModelView mv = new ModelView("mixedAuto.jsp");
        mv.addObject("id", id);
        mv.addObject("format", format);
        mv.addObject("limit", limit);
        mv.addObject("message", "ID: " + id + ", Format: " + format + ", Limit: " + limit);
        mv.addObject("method", "Injection automatique");
        return mv;
    }
}