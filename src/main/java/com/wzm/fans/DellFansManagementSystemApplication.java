package com.wzm.fans;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO Ipmitool 检测连接是否成功
//TODO 修改idrac.host后，自动刷新RedfishClient
//TODO 重构定时任务
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class DellFansManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DellFansManagementSystemApplication.class, args);
    }

}
