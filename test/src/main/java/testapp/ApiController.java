package testapp;

import com.monframework.JsonResponse;
import com.monframework.annotation.JsonAPI;
import com.monframework.annotation.RestController;
import com.monframework.annotation.GetMapping;
import com.monframework.annotation.PostMapping;
import com.monframework.annotation.PutMapping;
import com.monframework.annotation.DeleteMapping;
import com.monframework.annotation.PathVariable;
import com.monframework.annotation.RequestParam;
import testapp.models.Emp;
import testapp.models.Dept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {
    
    private List<Emp> employees = new ArrayList<>();
    private int nextId = 1;
    
    public ApiController() {
        // Données de test
        employees.add(new Emp(1, "John Doe"));
        employees.add(new Emp(2, "Jane Smith"));
        employees.add(new Emp(3, "Bob Johnson"));
        nextId = 4;
    }
    
    // Exemple 1: Retour simple avec annotation @JsonAPI
    @GetMapping("/api/hello")
    @JsonAPI(message = "Hello API!")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello World!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
    
    // Exemple 2: Retour d'une liste avec comptage automatique
    @GetMapping("/api/employees")
    public List<Emp> getAllEmployees() {
        return employees;
    }
    
    // Exemple 3: Retour avec JsonResponse personnalisé
    @GetMapping("/api/employees/{id}")
    public JsonResponse getEmployee(@PathVariable("id") int id) {
        Emp emp = employees.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
        
        if (emp == null) {
            return JsonResponse.notFound("Employee not found with id: " + id);
        }
        
        return JsonResponse.success(emp);
    }
    
    // Exemple 4: POST avec retour JsonResponse.created
    @PostMapping("/api/employees")
    public JsonResponse createEmployee(Emp emp) {
        emp.setId(nextId++);
        employees.add(emp);
        
        System.out.println("Employee created: " + emp);
        return JsonResponse.created(emp);
    }
    
    // Exemple 5: PUT avec annotation @JsonAPI personnalisée
    @PutMapping("/api/employees/{id}")
    @JsonAPI(statusCode = 200, message = "Employee updated successfully")
    public Emp updateEmployee(@PathVariable("id") int id, Emp updatedEmp) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == id) {
                updatedEmp.setId(id);
                employees.set(i, updatedEmp);
                System.out.println("Employee updated: " + updatedEmp);
                return updatedEmp;
            }
        }
        return null; // Retournera une erreur 404 automatiquement
    }
    
    // Exemple 6: DELETE avec code HTTP personnalisé
    @DeleteMapping("/api/employees/{id}")
    @JsonAPI(statusCode = 204, message = "Employee deleted")
    public void deleteEmployee(@PathVariable("id") int id) {
        employees.removeIf(emp -> emp.getId() == id);
        System.out.println("Employee deleted with id: " + id);
    }
    
    // Exemple 7: Recherche avec paramètres
    @GetMapping("/api/employees/search")
    public JsonResponse searchEmployees(@RequestParam("q") String query) {
        List<Emp> results = new ArrayList<>();
        for (Emp emp : employees) {
            if (emp.getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(emp);
            }
        }
        
        if (results.isEmpty()) {
            return JsonResponse.success("No employees found matching: " + query);
        }
        
        return JsonResponse.withCount(results, results.size());
    }
    
    // Exemple 8: Retour d'objets complexes
    @GetMapping("/api/department/{id}/employees")
    public JsonResponse getDepartmentEmployees(@PathVariable("id") int deptId) {
        // Simulation de données
        Dept dept = new Dept(deptId, "Department " + deptId);
        
        List<Emp> deptEmployees = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Emp emp = new Emp(100 + i, "Emp " + i + " from Dept " + deptId);
            emp.setDepartment(dept);
            deptEmployees.add(emp);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("department", dept);
        response.put("employees", deptEmployees);
        response.put("total", deptEmployees.size());
        
        return JsonResponse.success(response);
    }
    
    // Exemple 9: Gestion d'erreur
    @GetMapping("/api/error-test")
    public JsonResponse errorTest() {
        // Simuler une erreur
        boolean error = true;
        if (error) {
            return JsonResponse.error(400, "This is a simulated error");
        }
        return JsonResponse.success("No error");
    }
    
    // Exemple 10: Retour de données brutes (sans encapsulation JsonResponse)
    @GetMapping("/api/status")
    @JsonAPI
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("app", "Test API");
        status.put("version", "1.0.0");
        status.put("timestamp", System.currentTimeMillis());
        status.put("employeeCount", employees.size());
        return status;
    }
}