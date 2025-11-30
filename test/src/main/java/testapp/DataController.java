package testapp;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;

@Controller("/data")
public class DataController {
    
    @Route("/user")
    public User getUser() {
        return new User("John", "Doe", 30); // Retourne un objet
    }
    
    @Route("/list")
    public String[] getList() {
        return new String[]{"Item 1", "Item 2", "Item 3"}; // Retourne un tableau
    }
    
    @Route("/void")
    public void voidMethod() {
        System.out.println("Méthode void exécutée - pas de retour");
    }
}

// Classe POJO pour tester
class User {
    private String firstName;
    private String lastName;
    private int age;
    
    public User(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
    
    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getAge() { return age; }
    
    @Override
    public String toString() {
        return "User{firstName='" + firstName + "', lastName='" + lastName + "', age=" + age + "}";
    }
}