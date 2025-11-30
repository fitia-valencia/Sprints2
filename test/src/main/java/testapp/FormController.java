package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;
import com.monframework.annotation.RequestParam;
import com.monframework.ModelView;

@Controller("/form")
public class FormController {

    // Exemple: /form/search?q=java&page=2
    @Route("/search")
    public ModelView search(@RequestParam("q") String query,
                           @RequestParam(value = "page", defaultValue = "1") int page) {
        ModelView mv = new ModelView("search.jsp");
        mv.addObject("query", query);
        mv.addObject("page", page);
        mv.addObject("results", "Résultats pour: '" + query + "' - Page " + page);
        return mv;
    }

    // Exemple: /form/user?name=Alice&age=25&active=true
    @Route("/user")
    public ModelView userInfo(@RequestParam("name") String name,
                             @RequestParam(value = "age", defaultValue = "18") int age,
                             @RequestParam(value = "active", defaultValue = "false") boolean active) {
        ModelView mv = new ModelView("user.jsp");
        mv.addObject("name", name);
        mv.addObject("age", age);
        mv.addObject("active", active);
        mv.addObject("status", active ? "Actif" : "Inactif");
        return mv;
    }

    // Exemple avec paramètres optionnels
    @Route("/filter")
    public ModelView filterProducts(@RequestParam(value = "category", defaultValue = "all") String category,
                                   @RequestParam(value = "minPrice", defaultValue = "0") double minPrice,
                                   @RequestParam(value = "maxPrice", defaultValue = "1000") double maxPrice,
                                   @RequestParam(value = "inStock", defaultValue = "false") boolean inStock) {
        ModelView mv = new ModelView("filter.jsp");
        mv.addObject("category", category);
        mv.addObject("minPrice", minPrice);
        mv.addObject("maxPrice", maxPrice);
        mv.addObject("inStock", inStock);
        mv.addObject("message", "Filtres: " + category + " | Prix: $" + minPrice + "-$" + maxPrice + " | Stock: " + inStock);
        return mv;
    }
}