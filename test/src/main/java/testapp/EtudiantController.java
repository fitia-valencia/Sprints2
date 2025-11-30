package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;
import com.monframework.annotation.PathVariable;
import com.monframework.ModelView;

@Controller("/etudiant")
public class EtudiantController {

    @Route("/{id}")
    public ModelView getEtudiant(@PathVariable("id") int id) {
        ModelView mv = new ModelView("etudiant.jsp");
        mv.addObject("id", id);
        mv.addObject("nom", "Etudiant " + id);
        mv.addObject("age", 20 + id % 10); // Âge fictif
        mv.addObject("filiere", "Informatique");
        return mv;
    }
    
    @Route("/{id}/notes")
    public ModelView getNotes(@PathVariable("id") int id) {
        ModelView mv = new ModelView("notes.jsp");
        mv.addObject("etudiantId", id);
        mv.addObject("nom", "Etudiant " + id);
        mv.addObject("notes", new String[]{"Java: 15", "Web: 14", "BDD: 16"});
        return mv;
    }
    
    @Route("/{id}/cours/{coursId}")
    public ModelView getCours(@PathVariable("id") int etudiantId, 
                             @PathVariable("coursId") String coursId) {
        ModelView mv = new ModelView("cours.jsp");
        mv.addObject("etudiantId", etudiantId);
        mv.addObject("coursId", coursId);
        mv.addObject("coursNom", "Cours " + coursId);
        mv.addObject("presence", "75%");
        return mv;
    }
}