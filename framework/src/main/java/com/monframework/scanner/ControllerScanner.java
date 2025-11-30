package com.monframework.scanner;

import com.monframework.annotation.Controller;
import com.monframework.annotation.PathVariable;
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
            displayControllerInfo(controllerClass);
        }
    }

    private void displayControllerInfo(Class<?> controllerClass) {
        Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
        String baseUrl = controllerAnnotation.value();

        System.out.println("\n CONTRÔLEUR: " + controllerClass.getSimpleName());
        System.out.println("    URL de base: " + baseUrl);
        System.out.println("    Méthodes:");

        // Scanner les méthodes annotées @Route
        boolean hasRoutes = false;
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Route.class)) {
                Route routeAnnotation = method.getAnnotation(Route.class);
                String methodUrl = routeAnnotation.value();
                String fullUrl = baseUrl + methodUrl;

                // Normaliser l'URL
                fullUrl = fullUrl.replace("//", "/");

                RouteInfo routeInfo = new RouteInfo(fullUrl, method, method.getParameters());
                routes.add(routeInfo);

                System.out.println(" Route enregistrée: " + fullUrl + " -> " + method.getName());
                displayMethodParameters(method);
            } else {
                System.out.println("       " + method.getName() + "() -> NON ANNOTÉE");
            }
        }

        if (!hasRoutes) {
            System.out.println("        Aucune méthode annotée @Route trouvée");
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

    public RouteInfo findMatchingRoute(String actualUrl) {
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