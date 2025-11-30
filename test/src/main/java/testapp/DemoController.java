package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;
import com.monframework.ModelView;

@Controller("/demo")
public class DemoController {

    @Route("/sprint5")
    public ModelView pageSimple() {
        ModelView mv = new ModelView("sprint5.jsp");
        
        // Données simples vers la JSP
        mv.addObject("titre", "Bonjour depuis le contrôleur");
        mv.addObject("message", "Ceci est un message envoyé par le contrôleur");
        mv.addObject("nombre", 42);
        
        return mv;
    }
}