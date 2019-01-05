package com.perficient.library.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

public class PropertiesUtils {

    private PropertiesUtils() {
        throw new RuntimeException("PropertiesUtils cannot be initialized");
    }

    // map => key: fileName, value: resourceBundle
    private static Map<String, ResourceBundle> resourceBundleMap = new HashMap<String, ResourceBundle>();

    public static ResourceBundle getBundle(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("fileName cannot be blank");
        }
        ResourceBundle resourceBundle = null;
        if ((resourceBundle = resourceBundleMap.get(fileName)) != null) {
            return resourceBundle;
        }
        resourceBundle = ResourceBundle.getBundle(fileName);
        resourceBundleMap.put(fileName, resourceBundle);
        return resourceBundle;
    }

    public static String getValue(String fileName, String key) {
        return getBundle(fileName).getString(key);
    }

}
