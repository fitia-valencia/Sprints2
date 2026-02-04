package com.monframework.security;

import java.util.ArrayList;
import java.util.List;

public class SecurityConfig {
    
    // Noms des attributs de session (configurables)
    private static String sessionUserKey = "user";
    private static String sessionRolesKey = "roles";
    private static String sessionAuthKey = "authenticated";
    
    // Routes publiques par défaut
    private static List<String> publicUrls = new ArrayList<>();
    
    static {
        // URLs toujours publiques
        publicUrls.add("/login");
        publicUrls.add("/logout");
        publicUrls.add("/register");
        publicUrls.add("/api/public");
    }
    
    // Getters et setters
    public static String getSessionUserKey() {
        return sessionUserKey;
    }
    
    public static void setSessionUserKey(String key) {
        sessionUserKey = key;
    }
    
    public static String getSessionRolesKey() {
        return sessionRolesKey;
    }
    
    public static void setSessionRolesKey(String key) {
        sessionRolesKey = key;
    }
    
    public static String getSessionAuthKey() {
        return sessionAuthKey;
    }
    
    public static void setSessionAuthKey(String key) {
        sessionAuthKey = key;
    }
    
    public static List<String> getPublicUrls() {
        return publicUrls;
    }
    
    public static void addPublicUrl(String url) {
        publicUrls.add(url);
    }
    
    public static boolean isPublicUrl(String url) {
        return publicUrls.contains(url);
    }
}