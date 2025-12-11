package testapp;

import com.monframework.ModelView;
import com.monframework.annotation.Controller;
import com.monframework.annotation.GetMapping;
import com.monframework.annotation.PostMapping;
import testapp.models.Emp;
import testapp.models.Dept;
import java.util.List;
import java.util.Map;

@Controller
public class Sprint8BisController {
    
    // Page de test
    @GetMapping("/sprint8bis/test")
    public ModelView showTestPage() {
        return new ModelView("sprint8bis-form.jsp");
    }
    
    // Test 1: Un seul objet en paramètre
    @PostMapping("/sprint8bis/save-simple")
    public ModelView saveSimple(Emp emp) {
        System.out.println("=== TEST 1: Un seul objet ===");
        System.out.println("Emp reçu: " + emp);
        if (emp.getDepartment() != null) {
            System.out.println("Département: " + emp.getDepartment());
        }
        
        ModelView mv = new ModelView("sprint8bis-result.jsp");
        mv.addObject("test", "1 - Un seul objet");
        mv.addObject("emp", emp);
        return mv;
    }
    
    // Test 2: Deux objets en paramètres
    @PostMapping("/sprint8bis/save-multiple")
    public ModelView saveMultiple(Emp emp, Dept dept) {
        System.out.println("=== TEST 2: Deux objets ===");
        System.out.println("Emp reçu: " + emp);
        System.out.println("Dept reçu: " + dept);
        
        ModelView mv = new ModelView("sprint8bis-result.jsp");
        mv.addObject("test", "2 - Deux objets");
        mv.addObject("emp", emp);
        mv.addObject("dept", dept);
        return mv;
    }
    
    // Test 3: Mélange Map et objets
    @PostMapping("/sprint8bis/save-mixed")
    public ModelView saveMixed(Emp emp, Map<String, Object> extraData) {
        System.out.println("=== TEST 3: Mélange objet et Map ===");
        System.out.println("Emp reçu: " + emp);
        System.out.println("Extra data: " + extraData);
        
        ModelView mv = new ModelView("sprint8bis-result.jsp");
        mv.addObject("test", "3 - Mélange objet et Map");
        mv.addObject("emp", emp);
        mv.addObject("extraData", extraData);
        return mv;
    }
    
    // Test 4: Tableau dans l'objet
    @PostMapping("/sprint8bis/save-array")
    public ModelView saveWithArray(Emp emp) {
        System.out.println("=== TEST 4: Objet avec tableau ===");
        System.out.println("Emp reçu: " + emp);
        if (emp.getSkills() != null) {
            System.out.println("Skills: ");
            for (String skill : emp.getSkills()) {
                System.out.println("  - " + skill);
            }
        }
        
        ModelView mv = new ModelView("sprint8bis-result.jsp");
        mv.addObject("test", "4 - Objet avec tableau");
        mv.addObject("emp", emp);
        return mv;
    }
}