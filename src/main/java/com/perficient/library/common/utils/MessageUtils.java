package com.perficient.library.common.utils;

import java.util.ResourceBundle;

public class MessageUtils {

    private static final String FILE_NAME = "messages";

    private static ResourceBundle resourceBundle = null;

    private MessageUtils() {
        throw new RuntimeException("MessageUtils cannot be initialized");
    }

    public static final String getMessage(String key) {
        if (resourceBundle == null) {
            resourceBundle = PropertiesUtils.getBundle(FILE_NAME);
        }
        return resourceBundle.getString(key);
    }

}
