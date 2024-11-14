package com.wzm.fans.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "idrac")
public class IdracProperties {

    private String host;
    private String username;
    private String password;


}
