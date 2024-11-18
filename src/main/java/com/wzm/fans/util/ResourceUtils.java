package com.wzm.fans.util;

import java.net.URL;
import java.nio.file.Paths;

public class ResourceUtils {


    public static String absolutePath(String resource){
        URL resourceUrl = ResourceUtils.class.getClassLoader().getResource("application.yml");
        if (resourceUrl == null) {
            throw new RuntimeException ("Resource not found!");
        }
        return Paths.get(resourceUrl.getFile()).toAbsolutePath().toString();
    }
}
