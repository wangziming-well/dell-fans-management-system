package com.wzm.fans;

import com.wzm.fans.util.IPMI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan({"com.wzm.fans.properties"})
public class DellFansManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DellFansManagementSystemApplication.class, args);
        String host = "192.168.2.98";
        String username = "root";
        String password = "wang998321";
        IPMI ipmi = new IPMI(host, username, password);


    }

}
