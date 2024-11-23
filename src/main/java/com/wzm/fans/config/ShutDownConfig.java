package com.wzm.fans.config;

import com.wzm.fans.util.ConfigUtils;
import com.wzm.fans.util.IpmiTool;
import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class ShutDownConfig {

    private static final Log logger = LogFactory.getLog(ShutDownConfig.class);

    @PostConstruct
    public void registerShutdownHook() {
        logger.info("注册程序退出回调");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            boolean autoMode = ConfigUtils.getBoolean("shutdown.auto-mode");
            int fanSpeed = ConfigUtils.getInt("shutdown.fans-speed");
            if (autoMode){
                IpmiTool.setFansAutoMode();
                System.out.println("程序退出,服务器风扇设置为自动模式");
            } else{
                IpmiTool.setFansPWM(fanSpeed);
                System.out.println("程序退出,服务器风扇转速设置为:" + fanSpeed);

            }
        }));
    }
}
