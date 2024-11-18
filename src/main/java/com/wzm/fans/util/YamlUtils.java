package com.wzm.fans.util;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;


import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class YamlUtils {

    /**
     * 加载 YAML 文件
     */
    public static YAMLConfiguration loadYaml(String filePath) {
        try {
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<YAMLConfiguration> builder =
                    new FileBasedConfigurationBuilder<>(YAMLConfiguration.class)
                            .configure(params.fileBased().setFile(new File(filePath)));

            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存 YAML 文件
     */
    public static void saveYaml(YAMLConfiguration config, String outputPath) {
        try (Writer writer = new FileWriter(new File(outputPath))) {
            config.write(writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
