package com.monframework.scanner;

import com.monframework.annotation.Controller;
import com.monframework.annotation.DeleteMapping;
import com.monframework.annotation.GetMapping;
import com.monframework.annotation.PathVariable;
import com.monframework.annotation.PostMapping;
import com.monframework.annotation.PutMapping;
import com.monframework.annotation.RequestMapping;
import com.monframework.annotation.RequestParam;
import com.monframework.annotation.Route;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ControllerScanner {

    private Map<String, Method> routeMap = new HashMap<>();
    private List<RouteInfo> routes = new ArrayList<>();

    public void scanControllers(String packageName) {
        System.out.println(" SCAN DES CONTRÔLEURS DANS LE PACKAGE: " + packageName);

        Reflections reflections = new Reflections(packageName);

        // Trouver toutes les classes annotées @Controller
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        System.out.println(" Contrôleurs trouvés: " + controllers.size());

        for (Class<?> controllerClass : controllers) {
            scanController(controllerClass);
        }
    }

    private void scanController(Class<?> controllerClass) {
        Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
        String baseUrl = controllerAnnotation.value();

        System.out.println("\n CONTRÔLEUR: " + controllerClass.getSimpleName());
        System.out.println("    URL de base: " + baseUrl);
        System.out.println("    Méthodes HTTP:");

        for (Method method : controllerClass.getDeclaredMethods()) {
            scanMethodForHttpAnnotations(method, baseUrl);
        }
    }

    private void scanMethodForHttpAnnotations(Method method, String baseUrl) {
        String httpMethod = null;
        String methodUrl = null;

        // Vérifier les annotations HTTP spécifiques
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping annotation = method.getAnnotation(GetMapping.class);
            methodUrl = annotation.value();
            httpMethod = "GET";
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping annotation = method.getAnnotation(PostMapping.class);
            methodUrl = annotation.value();
            httpMethod = "POST";
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping annotation = method.getAnnotation(PutMapping.class);
            methodUrl = annotation.value();
            httpMethod = "PUT";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
            methodUrl = annotation.value();
            httpMethod = "DELETE";
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            methodUrl = annotation.value();
            httpMethod = annotation.method();
        } else if (method.isAnnotationPresent(Route.class)) {
            Route annotation = method.getAnnotation(Route.class);
            methodUrl = annotation.value();
            httpMethod = "GET"; // Par défaut
        }

        if (httpMethod != null && methodUrl != null) {
            String fullUrl = baseUrl + methodUrl;
            fullUrl = fullUrl.replace("//", "/");

            RouteInfo routeInfo = new RouteInfo(fullUrl, method, method.getParameters(), httpMethod);
            routes.add(routeInfo);

            System.out.println("      " + httpMethod + " " + fullUrl + " -> " + method.getName());
            displayMethodParameters(method);
        }
    }

    private void displayMethodParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            System.out.println("          Paramètres:");
            for (Parameter param : parameters) {
                String paramInfo = "            - " + param.getType().getSimpleName() + " " + param.getName();

                if (param.isAnnotationPresent(PathVariable.class)) {
                    PathVariable pathVar = param.getAnnotation(PathVariable.class);
                    paramInfo += " [@PathVariable '" + pathVar.value() + "']";
                } else if (param.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = param.getAnnotation(RequestParam.class);
                    paramInfo += " [@RequestParam '" + requestParam.value() + "'";
                    if (!requestParam.defaultValue().isEmpty()) {
                        paramInfo += ", default: '" + requestParam.defaultValue() + "'";
                    }
                    paramInfo += "]";
                }

                System.out.println(paramInfo);
            }
        }
    }

    public RouteInfo findMatchingRoute(String actualUrl, String httpMethod) {
        for (RouteInfo routeInfo : routes) {
            if (routeInfo.getPattern().matcher(actualUrl).matches() && 
                routeInfo.getHttpMethod().equalsIgnoreCase(httpMethod)) {
                return routeInfo;
            }
        }
        return null;
    }

    public RouteInfo findAnyMatchingRoute(String actualUrl) {
        for (RouteInfo routeInfo : routes) {
            if (routeInfo.getPattern().matcher(actualUrl).matches()) {
                return routeInfo;
            }
        }
        return null;
    }

    public List<RouteInfo> getRoutes() {
        return routes;
    }

    public Object executeMethod(String url) throws Exception {
        RouteInfo routeInfo = findAnyMatchingRoute(url);
        if (routeInfo == null) {
            throw new IllegalArgumentException("URL non trouvee: " + url);
        }

        Method method = routeInfo.getMethod();
        Class<?> controllerClass = method.getDeclaringClass();
        Object controllerInstance = controllerClass.newInstance();

        System.out.println("Execution de " + controllerClass.getSimpleName() + "." + method.getName());

        Object result = method.invoke(controllerInstance);

        System.out.println("Resultat: " + (result != null ? result.toString() : "null"));
        return result;
    }

    public Method getMethodForUrl(String url) {
        return routeMap.get(url);
    }

    public boolean urlExists(String url) {
        return findAnyMatchingRoute(url) != null;
    }

    public Map<String, Method> getRouteMap() {
        return routeMap;
    }
}