package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.GetMapping;
import com.monframework.annotation.PostMapping;
import com.monframework.annotation.PutMapping;
import com.monframework.annotation.DeleteMapping;
import com.monframework.annotation.RequestMapping;
import com.monframework.ModelView;
import testapp.models.User;

@Controller("/api")
public class HttpMethodController {

    // GET - Récupérer un utilisateur
    @GetMapping("/users/{id}")
    public ModelView getUser(int id) {
        ModelView mv = new ModelView("userDetail.jsp");
        mv.addObject("user", new User("User " + id, "user" + id + "@test.com", 20 + id));
        mv.addObject("method", "GET");
        return mv;
    }

    // GET - Liste des utilisateurs
    @GetMapping("/users")
    public ModelView getUsers() {
        ModelView mv = new ModelView("userList.jsp");
        mv.addObject("users", java.util.List.of(
            new User("Alice", "alice@test.com", 25),
            new User("Bob", "bob@test.com", 30)
        ));
        mv.addObject("method", "GET");
        return mv;
    }

    // POST - Créer un utilisateur
    @PostMapping("/users")
    public ModelView createUser(String name, String email, int age) {
        ModelView mv = new ModelView("userCreated.jsp");
        User newUser = new User(name, email, age);
        mv.addObject("user", newUser);
        mv.addObject("message", "Utilisateur créé avec succès");
        mv.addObject("method", "POST");
        return mv;
    }

    // PUT - Mettre à jour un utilisateur
    @PutMapping("/users/{id}")
    public ModelView updateUser(int id, String name, String email) {
        ModelView mv = new ModelView("userUpdated.jsp");
        mv.addObject("id", id);
        mv.addObject("name", name);
        mv.addObject("email", email);
        mv.addObject("message", "Utilisateur " + id + " mis à jour");
        mv.addObject("method", "PUT");
        return mv;
    }

    // DELETE - Supprimer un utilisateur
    @DeleteMapping("/users/{id}")
    public ModelView deleteUser(int id) {
        ModelView mv = new ModelView("userDeleted.jsp");
        mv.addObject("id", id);
        mv.addObject("message", "Utilisateur " + id + " supprimé");
        mv.addObject("method", "DELETE");
        return mv;
    }

    // RequestMapping avec méthode spécifiée
    @RequestMapping(value = "/products/{id}", method = "GET")
    public ModelView getProduct(int id) {
        ModelView mv = new ModelView("productDetail.jsp");
        mv.addObject("productId", id);
        mv.addObject("productName", "Product " + id);
        mv.addObject("method", "GET (RequestMapping)");
        return mv;
    }
}