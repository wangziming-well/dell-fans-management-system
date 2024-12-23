package com.wzm.fans.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzm.fans.pojo.AppConfig;
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

    private static volatile Map<String, Object> configMap;

    private static volatile long innerLastModified = 0;

    static {
        init();
    }

    /**
     * 初始化加载配置文件到应用中，并监听配置文件变动
     */
    private static void init() {
        File resource = ResourceUtils.getResource(configLocation);
        configMap = YamlUtils.loadYaml(resource);
        logger.info("加载配置文件:" + resource.getAbsolutePath());
        innerLastModified = resource.lastModified();
        FileMonitor.watch(resource, (ConfigUtils::configReload));
    }

    private static void configReload(File file) {
        writeLock.lock();
        try {
            if (innerLastModified == file.lastModified()){
                return; //说明是内部程序的修改
            }
            configMap = YamlUtils.loadYaml(file);
            logger.info("检测到配置文件变动，重新加载:" + file.getAbsolutePath());
        } finally {
            writeLock.unlock();
        }
    }

    public static AppConfig getAll(){
        return JacksonUtils.convertTo(configMap, AppConfig.class);
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

    public static  Map<String,Object> getMap(String key){
        return get(key, new TypeReference<>() {});
    }

    public static Map<String,String> getStringMap(String key){
        return get(key, new TypeReference<>() {});
    }


    public static <T> T get(String key, TypeReference<T> typeReference) {
        Object internal = getInternal(key);
        return convertObject(internal, typeReference);
    }

    private static <T> T convertObject(Object obj, TypeReference<T> typeReference) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(obj, typeReference);
    }
    @SuppressWarnings("unchecked")
    private static Object getInternal(String key) {
        readLock.lock();
        try {
            String[] split = key.split("\\.");
            Map<String, Object> map = ConfigUtils.configMap;
            for (int i = 0; i < split.length - 1; i++) {
                Object o = map.get(split[i]);
                if (!(o instanceof Map))
                    throw new RuntimeException("路径不存在:" + key);

                map = (Map<String, Object>) o;
            }

            // 获取最终值并检查是否为 null
            Object result = map.get(split[split.length - 1]);
            if (result == null)
                throw new RuntimeException("路径不存在/值为空");
            return result;
        } finally {
            readLock.unlock();
        }
    }


    public static void set(String key, Object value) {
        modifyConfig(() ->setInternal(key, value));
    }

    public static void setAll(AppConfig appConfig){
        modifyConfig(() ->
                ConfigUtils.configMap =
                JacksonUtils.convertTo(appConfig, new TypeReference<>() {}) );
    }

    /**
     * 修改配置并写到文件，{@code callback} 中有对{@link #configMap} 的修改，执行完{@code callback}后会将修改保存到配置文件
     * @param callback 对{@link #configMap} 的修改
     */
    private static void modifyConfig(Runnable callback){
        writeLock.lock();
        try {
            callback.run();
            File resource = ResourceUtils.getResource(configLocation);
            YamlUtils.saveYaml(ConfigUtils.configMap, resource);
            innerLastModified = resource.lastModified();
        } finally {
            writeLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
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
