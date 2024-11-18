package com.wzm.fans;

import com.wzm.fans.util.ResourceUtils;
import com.wzm.fans.util.YamlUtils;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.Map;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class DellFansManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DellFansManagementSystemApplication.class, args);
    }

}
