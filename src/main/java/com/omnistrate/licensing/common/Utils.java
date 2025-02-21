package com.omnistrate.licensing.common;

public class Utils {
    public static String getEnvOrDefault(String key, String defaultValue) {
        String envValue = System.getenv(key);
        if (isNullOrEmpty(envValue)) {
            return defaultValue;
        }
        return envValue;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}