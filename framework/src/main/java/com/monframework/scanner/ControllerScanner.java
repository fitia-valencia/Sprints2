package com.monframework.scanner;

import com.monframework.annotation.Controller;
import com.monframework.annotation.Route;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ControllerScanner {
    
    private Map<String, Method> routeMap = new HashMap<>();
    
    public void scanControllers(String packageName) {
        Reflections reflections = new Reflections(packageName);
        
        // Trouver toutes les classes annotées @Controller
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        System.out.println("Contrôleurs trouvés: " + controllers.size());
        
        for (Class<?> controllerClass : controllers) {
            Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
            String baseUrl = controllerAnnotation.url();
            
            System.out.println("Scan du contrôleur: " + controllerClass.getName() + " - baseUrl: " + baseUrl);
            
            // Scanner les méthodes annotées @Route
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Route.class)) {
                    Route routeAnnotation = method.getAnnotation(Route.class);
                    String methodUrl = routeAnnotation.url();
                    String fullUrl = baseUrl + methodUrl;
                    
                    // Normaliser l'URL (enlever les doubles slash)
                    fullUrl = fullUrl.replace("//", "/");
                    
                    routeMap.put(fullUrl, method);
                    System.out.println("Route mappée: " + fullUrl + " -> " + method.getName());
                }
            }
        }
    }
    
    public Method getMethodForUrl(String url) {
        return routeMap.get(url);
    }
    
    public boolean urlExists(String url) {
        return routeMap.containsKey(url);
    }
    
    public Map<String, Method> getRouteMap() {
        return routeMap;
    }
}