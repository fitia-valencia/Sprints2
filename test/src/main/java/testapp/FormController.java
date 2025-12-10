package testapp;

import com.monframework.ModelView;
import com.monframework.annotation.Controller;
import com.monframework.annotation.PostMapping;
import com.monframework.annotation.RequestParam;
import java.util.Arrays;
import java.util.Map;

@Controller
public class FormController {
    
    @PostMapping("/submit-form")
    public ModelView submitForm(Map<String, Object> formData) {
        ModelView mv = new ModelView("testResult.jsp");
        
        // Traiter les données du formulaire
        System.out.println("Données reçues: " + formData);
        
        // Gestion des checkbox
        Object hobbies = formData.get("hobbies");
        if (hobbies != null) {
            if (hobbies instanceof String[]) {
                String[] hobbiesArray = (String[]) hobbies;
                System.out.println("Hobbies sélectionnés: " + Arrays.toString(hobbiesArray));
            } else if (hobbies instanceof String) {
                System.out.println("Un seul hobby: " + hobbies);
            }
        }
        
        // Ajouter toutes les données au modèle
        mv.addAllObjects(formData);
        
        return mv;
    }
    
    // Pour démontrer la compatibilité avec l'ancien système - SANS "required"
    @PostMapping("/old-submit")
    public ModelView oldSubmit(
        @RequestParam("name") String name,
        @RequestParam(value = "age", defaultValue = "0") int age,
        @RequestParam("hobbies") String[] hobbies) {  // Enlevé: required = false
        
        ModelView mv = new ModelView("oldResult.jsp");
        mv.addObject("name", name);
        mv.addObject("age", age);
        mv.addObject("hobbies", hobbies);
        
        return mv;
    }
}