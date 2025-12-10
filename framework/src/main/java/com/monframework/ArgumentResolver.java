package com.monframework;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

import com.monframework.annotation.PathVariable;
import com.monframework.annotation.RequestParam;

public class ArgumentResolver {
    
    public static Object[] resolveParameters(Method method, 
                                            HttpServletRequest request, 
                                            Map<String, String[]> pathParams) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        // Récupérer tous les paramètres de la requête
        Map<String, String[]> requestParams = request.getParameterMap();
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class<?> type = param.getType();
            
            // SPRINT 8: Si c'est un Map<String, Object> ou Map pour toutes les données
            if (Map.class.isAssignableFrom(type)) {
                Map<String, Object> allData = new HashMap<>();
                
                // Ajouter les paramètres de requête
                for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                    String[] values = entry.getValue();
                    if (values == null || values.length == 0) {
                        continue;
                    }
                    if (values.length == 1) {
                        allData.put(entry.getKey(), values[0]);
                    } else {
                        allData.put(entry.getKey(), values);
                    }
                }
                
                // Ajouter les paramètres du chemin (de type {id} dans l'URL)
                if (pathParams != null) {
                    for (Map.Entry<String, String[]> entry : pathParams.entrySet()) {
                        String[] values = entry.getValue();
                        if (values != null && values.length > 0) {
                            allData.put(entry.getKey(), values[0]);
                        }
                    }
                }
                
                args[i] = allData;
                continue;
            }
        }
        return args;
    }
}