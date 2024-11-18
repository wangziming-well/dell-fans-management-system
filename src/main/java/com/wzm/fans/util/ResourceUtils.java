package com.wzm.fans.util;

import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class ResourceUtils {

    private static final ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();

    public static File getResource(String location){
        try {
            return  resourceLoader.getResource(location).getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
