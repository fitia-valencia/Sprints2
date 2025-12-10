package testapp;

import com.monframework.ModelView;
import com.monframework.annotation.Controller;
import com.monframework.annotation.GetMapping;
import com.monframework.annotation.PostMapping;
import com.monframework.annotation.RequestParam;
import java.util.Arrays;
import java.util.Map;

@Controller
public class Sprint8Controller {
    
    // Page d'accueil du formulaire
    @GetMapping("/sprint8/form")
    public ModelView showForm() {
        System.out.println("=== SPRINT 8: Affichage du formulaire ===");
        return new ModelView("sprint8-form.jsp");
    }
    
    // SPRINT 8: Méthode avec Map<String, Object>
    @PostMapping("/sprint8/process")
    public ModelView processForm(Map<String, Object> formData) {
        System.out.println("=== SPRINT 8: Traitement avec Map ===");
        System.out.println("Taille du Map: " + formData.size());
        
        // Afficher toutes les données reçues
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            if (entry.getValue() instanceof String[]) {
                System.out.println("  " + entry.getKey() + " = " + Arrays.toString((String[]) entry.getValue()));
            } else {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
        }
        
        // Analyser les checkbox
        analyzeCheckboxes(formData);
        
        // Créer ModelView
        ModelView mv = new ModelView("sprint8-result.jsp");
        
        // SPRINT 8: Ajouter toutes les données d'un coup
        mv.addAllObjects(formData);
        mv.addObject("formData", formData);
        // Ajouter des métadonnées
        mv.addObject("method", "Map<String, Object>");
        mv.addObject("sprint", 8);
        
        return mv;
    }
    
    // Ancienne méthode pour comparaison - SANS "required"
    @PostMapping("/sprint8/process-old")
    public ModelView processOldMethod(
            @RequestParam("name") String name,
            @RequestParam(value = "age", defaultValue = "0") int age,
            @RequestParam("hobbies") String[] hobbies) {  // Enlevé: required = false
        
        System.out.println("=== Ancienne méthode (sprint 6) ===");
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Hobbies: " + (hobbies != null ? Arrays.toString(hobbies) : "null"));
        
        ModelView mv = new ModelView("sprint8-old-result.jsp");
        mv.addObject("name", name);
        mv.addObject("age", age);
        mv.addObject("hobbies", hobbies);
        
        return mv;
    }
    
    // Méthode avec mélange Map + paramètres individuels - SANS "required"
    @PostMapping("/sprint8/process-mixed")
    public ModelView processMixed(
            @RequestParam("username") String username,
            Map<String, Object> allData,
            @RequestParam(value = "newsletter", defaultValue = "false") String newsletter) { // Changé de boolean à String
        
        System.out.println("=== Méthode mixte ===");
        System.out.println("Username: " + username);
        System.out.println("Newsletter: " + newsletter);
        System.out.println("All data size: " + allData.size());
        
        ModelView mv = new ModelView("sprint8-mixed-result.jsp");
        mv.addObject("username", username);
        mv.addObject("newsletter", newsletter);
        mv.addAllObjects(allData);
        
        return mv;
    }
    
    // Méthode utilitaire pour analyser les checkbox
    private void analyzeCheckboxes(Map<String, Object> formData) {
        System.out.println("--- Analyse des checkbox ---");
        
        // Vérifier si une checkbox est cochée
        if (formData.containsKey("accept")) {
            Object acceptValue = formData.get("accept");
            System.out.println("Checkbox 'accept': " + acceptValue);
            if (acceptValue != null) {
                System.out.println("  -> Accepté: " + !"false".equals(acceptValue));
            }
        } else {
            System.out.println("Checkbox 'accept': NON COCHÉE (pas dans le Map)");
        }
        
        // Checkbox multiples
        if (formData.containsKey("interests")) {
            Object interests = formData.get("interests");
            if (interests instanceof String[]) {
                String[] interestsArray = (String[]) interests;
                System.out.println("Intérêts sélectionnés: " + interestsArray.length);
                for (String interest : interestsArray) {
                    System.out.println("  - " + interest);
                }
            }
        }
    }
}