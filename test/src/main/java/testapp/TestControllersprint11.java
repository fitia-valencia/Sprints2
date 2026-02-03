package testapp;

import com.monframework.ModelView;
import com.monframework.ModelView;
import com.monframework.annotation.Controller;
import com.monframework.annotation.GetMapping;

@Controller
public class TestControllersprint11 {
    @GetMapping("/session-test")
    public ModelView sessionTest(com.monframework.session.MySession session) {

        session.set("nom", "Fitia");
        session.set("niveau", "L2");

        ModelView mv = new ModelView("session.jsp");
        return mv;
    }

}
