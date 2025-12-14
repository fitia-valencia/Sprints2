package com.monframework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;

public class JsonUtil {
    private static ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }
    
    public static String toJson(Object object) {
        try {
            if (object instanceof JsonResponse) {
                return objectMapper.writeValueAsString(((JsonResponse) object).toMap());
            }
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Erreur de sérialisation JSON: " + e.getMessage() + "\"}";
        }
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}