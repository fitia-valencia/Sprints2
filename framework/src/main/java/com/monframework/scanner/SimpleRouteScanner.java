package com.monframework.scanner;

import com.monframework.annotation.Route;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class SimpleRouteScanner {
    public static Set<Method> findAnnotatedMethods(String packageName) throws Exception {
        Set<Method> methods = new HashSet<>();
        String path = packageName.replace('.', '/');
        File directory = new File("target/classes/" + path);
        
        System.out.println("Scanning directory: " + directory.getAbsolutePath());
        
        if (directory.exists()) {
            scanDirectory(directory, packageName, methods);
        }
        return methods;
    }
    
    private static void scanDirectory(File dir, String packageName, Set<Method> methods) throws Exception {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), methods);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                checkClass(className, methods);
            }
        }
    }
    
    private static void checkClass(String className, Set<Method> methods) throws Exception {
        Class<?> clazz = Class.forName(className);
        for (Method method : clazz.getDeclaredMethods()) {
            Route route = method.getAnnotation(Route.class);
            if (route != null) {
                methods.add(method);
            }
        }
    }
}