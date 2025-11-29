package com.monframework.scanner;

import com.monframework.annotation.Route;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.Set;

public class RouteScanner {
    public static Set<Method> findAnnotatedMethods(String packageName) {
        Reflections reflections = new Reflections(packageName, 
            Scanners.MethodsAnnotated, 
            Scanners.TypesAnnotated, 
            Scanners.SubTypes);
        
        System.out.println("Scanning package: " + packageName);
        
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Route.class);
        System.out.println("Found " + methods.size() + " annotated methods");
        
        return methods;
    }
}