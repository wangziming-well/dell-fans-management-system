package com.wzm.fans.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

public abstract class JacksonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }


    public static Map<String, String> convertToStrMap(Object obj) {
        return mapper.convertValue(obj, new TypeReference<>() {});
    }

    public static <T> T convertTo(Object obj,TypeReference<T> reference) {
        return mapper.convertValue(obj, reference);
    }

    public static <T> T convertTo(Object obj,Class<T> clazz) {
        return mapper.convertValue(obj, clazz);
    }


    public static Map<String, String> toStringMap(Object obj) {
        return mapper.convertValue(obj, new TypeReference<>() {});
    }

    public static Map<String, String> parseToStringMap(String str) {
        try {
            return mapper.readValue(str, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> parseToMap(String str) {
        try {
            return mapper.readValue(str, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, String>> parseToMapList(String str) {
        try {
            return mapper.readValue(str, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseToList(Class<T> clazz, String jsonStr) {
        try {
            return mapper.readValue(jsonStr, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String toJsonString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
