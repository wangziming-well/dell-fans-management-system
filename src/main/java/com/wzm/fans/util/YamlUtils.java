package com.wzm.fans.util;




import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlUtils {

    /**
     * 加载 YAML 文件
     */
    public static Map<String,Object> loadYaml(File file) {
        Yaml yaml = new Yaml();
        try {
            return yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存 YAML 文件
     */
    public static void saveYaml(Map<String,Object>  configMap, File file) {
        DumperOptions options = new DumperOptions();
        options.setIndent(2); // 设置缩进为 2
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // 使用 Block Style
        options.setPrettyFlow(false); // 不启用紧凑美化输出

        Yaml yamlWriter = new Yaml(options);
        try (FileWriter writer = new FileWriter(file)) {
            yamlWriter.dump(configMap, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
