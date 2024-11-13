package com.wzm.fans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan({"com.wzm.fans.properties"})
public class DellFansManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DellFansManagementSystemApplication.class, args);

    }

}
