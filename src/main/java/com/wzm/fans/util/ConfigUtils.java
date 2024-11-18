package com.wzm.fans.util;

import com.wzm.fans.service.IpmiService;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConfigUtils {

    private static final String configLocation = "file:config.yml";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    private static final Log logger = LogFactory.getLog(IpmiService.class);


    private static final YAMLConfiguration yamlConfiguration;
    static {
        File resource = ResourceUtils.getResource(configLocation);
        logger.info("加载配置文件:" +resource.getAbsolutePath());
        yamlConfiguration = YamlUtils.loadYaml(resource);
    }

    public static String get(String key) {
        readLock.lock();
        try {
            return yamlConfiguration.getString(key);
        } finally {
            readLock.unlock();
        }
    }

    public static int getInt(String key) {
        readLock.lock();
        try {
            return yamlConfiguration.getInt(key);
        } finally {
            readLock.unlock();
        }
    }

    public static double getDouble(String key) {
        readLock.lock();
        try {
            return yamlConfiguration.getDouble(key);
        } finally {
            readLock.unlock();
        }
    }

    public static <T> T get(String key, Class<T> clazz) {
        readLock.lock();
        try {
            return yamlConfiguration.get(clazz, key);
        } finally {
            readLock.unlock();
        }
    }

    public static void set(String key, Object value) {

        writeLock.lock();
        try {
            yamlConfiguration.setProperty(key, value);
            YamlUtils.saveYaml(ConfigUtils.yamlConfiguration, ResourceUtils.getResource(configLocation));
        } finally {
            writeLock.unlock();
        }

    }


}
