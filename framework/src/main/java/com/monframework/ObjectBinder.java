package com.monframework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class ObjectBinder {

    public static Object bind(Class<?> clazz, String paramName, HttpServletRequest request) throws Exception {
        Object instance = clazz.newInstance();
        Map<String, String[]> params = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values == null || values.length == 0)
                continue;

            // Vérifier si ce paramètre correspond à notre objet
            if (key.startsWith(paramName + ".") || !key.contains(".")) {
                String propertyPath = key;
                if (key.startsWith(paramName + ".")) {
                    propertyPath = key.substring(paramName.length() + 1);
                }

                // Gérer les propriétés imbriquées
                setNestedProperty(instance, propertyPath, values);
            }
        }

        return instance;
    }

    private static void setNestedProperty(Object obj, String propertyPath, String[] values) throws Exception {
        if (!propertyPath.contains(".")) {
            // Propriété simple
            setSimpleProperty(obj, propertyPath, values);
        } else {
            // Propriété imbriquée
            String[] parts = propertyPath.split("\\.", 2);
            String currentProp = parts[0];
            String remainingPath = parts[1];

            // Récupérer ou créer l'objet imbriqué
            Object nestedObj = getOrCreateNestedObject(obj, currentProp);

            // Continuer récursivement
            setNestedProperty(nestedObj, remainingPath, values);
        }
    }

    private static Object getOrCreateNestedObject(Object obj, String propertyName) throws Exception {
        Class<?> clazz = obj.getClass();

        // Chercher le getter
        String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method getter = null;

        try {
            getter = clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            // Essayer avec "is" pour les boolean
            getterName = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            try {
                getter = clazz.getMethod(getterName);
            } catch (NoSuchMethodException e2) {
                // Pas de getter, utiliser le champ directement
                Field field = clazz.getDeclaredField(propertyName);
                field.setAccessible(true);
                Object nestedObj = field.get(obj);
                if (nestedObj == null) {
                    nestedObj = field.getType().newInstance();
                    field.set(obj, nestedObj);
                }
                return nestedObj;
            }
        }

        // Appeler le getter
        Object nestedObj = getter.invoke(obj);
        if (nestedObj == null) {
            // Créer une nouvelle instance
            Class<?> nestedType = getter.getReturnType();
            nestedObj = nestedType.newInstance();

            // Chercher le setter
            String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            Method setter = clazz.getMethod(setterName, nestedType);
            setter.invoke(obj, nestedObj);
        }

        return nestedObj;
    }

    private static void setSimpleProperty(Object obj, String propertyName, String[] values) throws Exception {
        Class<?> clazz = obj.getClass();

        // Chercher le setter
        String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];

                if (paramType.isArray() && paramType.getComponentType() == String.class) {
                    // Tableau de String
                    method.invoke(obj, (Object) values);
                } else if (values.length > 0) {
                    // Valeur simple
                    Object convertedValue = convertValue(values[0], paramType);
                    method.invoke(obj, convertedValue);
                }
                return;
            }
        }

        // Si pas de setter, essayer d'accéder au champ directement
        try {
            Field field = clazz.getDeclaredField(propertyName);
            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            if (fieldType.isArray() && fieldType.getComponentType() == String.class) {
                field.set(obj, values);
            } else if (values.length > 0) {
                Object convertedValue = convertValue(values[0], fieldType);
                field.set(obj, convertedValue);
            }
        } catch (NoSuchFieldException e) {
            // Champ non trouvé, ignorer
        }
    }

    private static Object convertValue(String value, Class<?> targetType) {
        if (value == null)
            return null;

        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value);
        }

        return value;
    }
}