package com.johnmartin.coaching.utilities;

import java.lang.reflect.Field;
import java.util.Collection;

public final class LogMaskUtility {

    private static final int MAX_COLLECTION_SIZE = 10;
    private static final int MAX_DEPTH = 3;

    private LogMaskUtility() {
    }

    public static String mask(Object obj) {
        return mask(obj, 0);
    }

    private static String mask(Object obj, int depth) {
        if (obj == null) {
            return "null";
        }

        // Prevent deep recursion
        if (depth > MAX_DEPTH) {
            return "...";
        }

        // Skip simple types
        if (isSimpleType(obj)) {
            return obj.toString();
        }

        // Handle collections with limit
        if (obj instanceof Collection<?> collection) {
            StringBuilder sb = new StringBuilder("[");
            int count = 0;

            for (Object item : collection) {
                if (count >= MAX_COLLECTION_SIZE) {
                    sb.append("... (truncated)");
                    break;
                }

                sb.append(mask(item, depth + 1)).append(", ");
                count++;
            }

            if (sb.length() > 1 && sb.charAt(sb.length() - 2) == ',') {
                sb.setLength(sb.length() - 2); // remove trailing comma
            }

            return sb.append("]").toString();
        }

        // Handle arrays (Object[] only)
        if (obj.getClass().isArray() && obj instanceof Object[] arr) {
            StringBuilder sb = new StringBuilder("[");
            int len = Math.min(arr.length, MAX_COLLECTION_SIZE);

            for (int i = 0; i < len; i++) {
                sb.append(mask(arr[i], depth + 1)).append(", ");
            }

            if (arr.length > MAX_COLLECTION_SIZE) {
                sb.append("... (truncated)");
            } else if (sb.length() > 1) {
                sb.setLength(sb.length() - 2);
            }

            return sb.append("]").toString();
        }

        // Handle objects via reflection
        Class<?> clazz = obj.getClass();
        StringBuilder result = new StringBuilder(clazz.getSimpleName()).append("[");

        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                if (field.isAnnotationPresent(com.johnmartin.coaching.annotations.Sensitive.class)) {
                    value = "***";
                } else {
                    value = mask(value, depth + 1);
                }

                result.append(field.getName()).append("=").append(value);
            } catch (Exception e) {
                result.append(field.getName()).append("=ERROR");
            }

            if (i < fields.length - 1) {
                result.append(", ");
            }
        }

        return result.append("]").toString();
    }

    private static boolean isSimpleType(Object obj) {
        return obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Enum
               || obj.getClass().isPrimitive();
    }
}
