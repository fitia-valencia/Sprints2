package com.monframework;

import java.util.HashMap;
import java.util.Map;

public class JsonResponse {
    private String status;
    private int code;
    private String message;
    private Object data;
    private Integer count;
    private Map<String, Object> metadata;
    
    // Constructeurs
    public JsonResponse() {
        this.metadata = new HashMap<>();
    }
    
    public JsonResponse(String status, int code, String message) {
        this();
        this.status = status;
        this.code = code;
        this.message = message;
    }
    
    public JsonResponse(String status, int code, String message, Object data) {
        this(status, code, message);
        this.data = data;
    }
    
    // Méthodes statiques pour créer des réponses standard
    public static JsonResponse success(Object data) {
        return new JsonResponse("success", 200, "Opération réussie", data);
    }
    
    public static JsonResponse success(String message, Object data) {
        return new JsonResponse("success", 200, message, data);
    }
    
    public static JsonResponse success(String message) {
        return new JsonResponse("success", 200, message);
    }
    
    public static JsonResponse error(int code, String message) {
        return new JsonResponse("error", code, message);
    }
    
    public static JsonResponse error(String message) {
        return new JsonResponse("error", 500, message);
    }
    
    public static JsonResponse notFound(String message) {
        return new JsonResponse("error", 404, message);
    }
    
    public static JsonResponse badRequest(String message) {
        return new JsonResponse("error", 400, message);
    }
    
    public static JsonResponse created(Object data) {
        return new JsonResponse("success", 201, "Créé avec succès", data);
    }
    
    public static JsonResponse withCount(Object data, int count) {
        JsonResponse response = success(data);
        response.setCount(count);
        return response;
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    // Méthode pour convertir en Map (pour Jackson)
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("code", code);
        map.put("message", message);
        
        if (data != null) {
            map.put("data", data);
        }
        
        if (count != null) {
            map.put("count", count);
        }
        
        if (!metadata.isEmpty()) {
            map.put("metadata", metadata);
        }
        
        return map;
    }
}