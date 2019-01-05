package com.perficient.library.common.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class JacksonUtils {

    private JacksonUtils() {
        throw new RuntimeException("JacksonUtils cannot be initialized");
    }

    public static <T> T getObject(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        // ignore unknown properties in JSON object
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        T result = null;
        try {
            result = mapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> readJsonToMap(String json) {
        Map<String, Object> maps = new HashMap<String, Object>();
        if (!StringUtils.isNotBlank(json)) {
            return maps;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            maps = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maps;
    }
}
