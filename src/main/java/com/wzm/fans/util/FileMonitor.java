package com.wzm.fans.util;

import org.apache.commons.io.monitor.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.function.Consumer;

public class FileMonitor {

    private static final Log logger = LogFactory.getLog(FileMonitor.class);

    public static void watch(File file, Consumer<File> callback){
        File innerFile = file.getAbsoluteFile();
        FileAlterationObserver observer = new FileAlterationObserver(innerFile.getParentFile());
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File f) {
                if (f.getName().equals(innerFile.getName())) {
                    callback.accept(f);
                }
            }
        });
        FileAlterationMonitor monitor = new FileAlterationMonitor(3000, observer);
        try {
            monitor.start();
            logger.info("文件监听器已启动，监控文件: " + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("启动文件监听器失败: " + e.getMessage(), e);
            throw new RuntimeException("启动文件监听器失败，请检查文件路径和权限", e);
        }
    }
}