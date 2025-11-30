package com.monframework.scanner;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.monframework.annotation.RequestParam;

public class RouteInfo {
    private String urlPattern; // "etudiant/{id}"
    private Method method;
    private List<String> pathVariables; // ["id"]
    private Parameter[] parameters;

    public RouteInfo(String urlPattern, Method method, Parameter[] parameters) {
        this.urlPattern = urlPattern;
        this.method = method;
        this.parameters = parameters;
        this.pathVariables = extractPathVariables(urlPattern);
    }

    private List<String> extractPathVariables(String urlPattern) {
        List<String> variables = new ArrayList<>();
        // Exemple: "etudiant/{id}" -> on cherche les parties entre {}
        String[] parts = urlPattern.split("/");
        for (String part : parts) {
            if (part.startsWith("{") && part.endsWith("}")) {
                variables.add(part.substring(1, part.length() - 1));
            }
        }
        return variables;
    }

    // Convertir le pattern en regex pour le matching
    public Pattern getPattern() {
        String regex = urlPattern.replaceAll("\\{.*?\\}", "([^/]+)");
        return Pattern.compile(regex);
    }

    // Extraire les valeurs des variables du path
    public java.util.Map<String, String> extractPathVariablesValues(String actualUrl) {
        java.util.Map<String, String> values = new java.util.HashMap<>();
        Pattern pattern = getPattern();
        java.util.regex.Matcher matcher = pattern.matcher(actualUrl);
        if (matcher.matches()) {
            for (int i = 0; i < pathVariables.size(); i++) {
                String variableName = pathVariables.get(i);
                String variableValue = matcher.group(i + 1);
                values.put(variableName, variableValue);
            }
        }
        return values;
    }

    public boolean isRequestParam(Parameter param) {
        return param.isAnnotationPresent(RequestParam.class);
    }

    public String getRequestParamName(Parameter param) {
        RequestParam annotation = param.getAnnotation(RequestParam.class);
        return annotation != null ? annotation.value() : null;
    }

    public String getRequestParamDefaultValue(Parameter param) {
        RequestParam annotation = param.getAnnotation(RequestParam.class);
        return annotation != null ? annotation.defaultValue() : null;
    }

    public String getUrlPattern() { return urlPattern; }
    public Method getMethod() { return method; }
    public List<String> getPathVariables() { return pathVariables; }
    public Parameter[] getParameters() { return parameters; }
}