package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;
import com.monframework.ModelView;

@Controller("/test")
public class TestErrorController {

    // Test avec paramètre manquant (devrait utiliser la valeur par défaut 0)
    @Route("/missing-param")
    public ModelView testMissingParam(int id, String name) {
        ModelView mv = new ModelView("testResult.jsp");
        mv.addObject("id", id);
        mv.addObject("name", name);
        mv.addObject("message", "ID: " + id + ", Name: " + (name != null ? name : "null"));
        return mv;
    }

    // Test avec conversion d'erreur
    @Route("/convert-error")
    public ModelView testConvertError(String text, int number, boolean flag) {
        ModelView mv = new ModelView("testResult.jsp");
        mv.addObject("text", text);
        mv.addObject("number", number);
        mv.addObject("flag", flag);
        mv.addObject("message", "Text: " + text + ", Number: " + number + ", Flag: " + flag);
        return mv;
    }
}