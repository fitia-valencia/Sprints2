package testapp.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Emp {
    private int id;
    private String name;
    private Dept department;
    private String[] skills;
    
    // Constructeurs
    public Emp() {}
    
    public Emp(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Dept getDepartment() { return department; }
    public void setDepartment(Dept department) { this.department = department; }
    
    public String[] getSkills() { return skills; }
    public void setSkills(String[] skills) { this.skills = skills; }
    
    @Override
    public String toString() {
        return "Emp{id=" + id + ", name='" + name + "', department=" + 
               (department != null ? department.toString() : "null") + "}";
    }
}