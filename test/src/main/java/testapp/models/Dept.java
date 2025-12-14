package testapp.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dept {
    private int id;
    private String name;
    
    // Constructeurs
    public Dept() {}
    
    public Dept(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Dept{id=" + id + ", name='" + name + "'}";
    }
}