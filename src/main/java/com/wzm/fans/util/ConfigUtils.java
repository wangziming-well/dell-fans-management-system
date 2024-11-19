package com.wzm.fans.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConfigUtils {

    private static final String configLocation = "file:config.yml";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    private static final Log logger = LogFactory.getLog(ConfigUtils.class);


    private static final Map<String, Object> configMap;

    static {
        File resource = ResourceUtils.getResource(configLocation);
        logger.info("加载配置文件:" + resource.getAbsolutePath());
        configMap = YamlUtils.loadYaml(resource);
    }

    public static String get(String key) {
        return getInternal(key).toString();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static double getDouble(String key) {
        return Double.parseDouble(get(key));
    }

    public static <T> T get(String key, TypeReference<T> typeReference) {
        Object internal = getInternal(key);
        return convertObject(internal, typeReference);
    }

    private static <T> T convertObject(Object obj, TypeReference<T> typeReference) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(obj, typeReference);
    }

    private static Object getInternal(String key) {
        readLock.lock();
        try {
            String[] split = key.split("\\.");
            Map<String, Object> map = ConfigUtils.configMap;
            for (int i = 0; i < split.length - 1; i++) {
                Object o = map.get(split[i]);
                if (!(o instanceof Map))
                    throw new RuntimeException("路径不存在");
                map = (Map<String, Object>) o;
            }

            // 获取最终值并检查是否为 null
            Object result = map.get(split[split.length - 1]);
            if (result == null)
                throw new RuntimeException("路径不存在");
            return result;
        } finally {
            readLock.unlock();
        }
    }

    public static void set(String key, Object value) {
        writeLock.lock();
        try {
            setInternal(key, value);
            File resource = ResourceUtils.getResource(configLocation);
            YamlUtils.saveYaml(ConfigUtils.configMap, resource);
        } finally {
            writeLock.unlock();
        }
    }

    private static void setInternal(String key, Object value) {
        String[] split = key.split("\\.");
        Map<String, Object> map = ConfigUtils.configMap;
        for (int i = 0; i < split.length - 1; i++) {
            String currKey = split[i];
            Object o = map.get(currKey);
            // 如果路径不存在或者不是 Map，则创建新的 Map
            if (!(o instanceof Map)) {
                Map<String, Object> newMap = new HashMap<>();
                map.put(currKey, newMap);
                map = newMap;
            } else {
                map = (Map<String, Object>) o;
            }
        }
        map.put(split[split.length - 1], value);
    }
}
