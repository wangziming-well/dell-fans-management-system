package com.wzm.fans.service;

import com.wzm.fans.util.ResourceUtils;
import com.wzm.fans.util.YamlUtils;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
public class ConfigService {

    private static final String configLocation = "classpath:config.yml";


    private final YAMLConfiguration yamlConfiguration ;

    public ConfigService() {
        try {
            this.yamlConfiguration = YamlUtils.loadYaml(ResourceUtils.absolutePath(configLocation) );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key){
        return yamlConfiguration.getString(key);
    }



}
