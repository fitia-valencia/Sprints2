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
        System.out.println(" SCAN DES CONTRÔLEURS DANS LE PACKAGE: " + packageName);
        
        Reflections reflections = new Reflections(packageName);
        
        // Trouver toutes les classes annotées @Controller
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        System.out.println(" Contrôleurs trouvés: " + controllers.size());
        
        for (Class<?> controllerClass : controllers) {
            displayControllerInfo(controllerClass);
        }
    }
    
    private void displayControllerInfo(Class<?> controllerClass) {
        Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
        String baseUrl = controllerAnnotation.url();
        
        System.out.println("\n CONTRÔLEUR: " + controllerClass.getSimpleName());
        System.out.println("    URL de base: " + baseUrl);
        System.out.println("    Méthodes:");
        
        // Scanner les méthodes annotées @Route
        boolean hasRoutes = false;
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Route.class)) {
                Route routeAnnotation = method.getAnnotation(Route.class);
                String methodUrl = routeAnnotation.url();
                String fullUrl = baseUrl + methodUrl;
                
                // Normaliser l'URL
                fullUrl = fullUrl.replace("//", "/");
                
                routeMap.put(fullUrl, method);
                System.out.println("       " + method.getName() + "() -> " + fullUrl);
                hasRoutes = true;
            } else {
                System.out.println("       " + method.getName() + "() -> NON ANNOTÉE");
            }
        }
        
        if (!hasRoutes) {
            System.out.println("        Aucune méthode annotée @Route trouvée");
        }
    }

    public Object executeMethod(String url) throws Exception {
        if (!urlExists(url)) {
            throw new IllegalArgumentException("URL non trouvée: " + url);
        }
        
        Method method = routeMap.get(url);
        Class<?> controllerClass = method.getDeclaringClass();
        Object controllerInstance = controllerClass.newInstance();
        
        System.out.println(" Exécution de " + controllerClass.getSimpleName() + "." + method.getName());
        
        Object result = method.invoke(controllerInstance);
        
        System.out.println(" Résultat: " + (result != null ? result.toString() : "null"));
        return result;
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