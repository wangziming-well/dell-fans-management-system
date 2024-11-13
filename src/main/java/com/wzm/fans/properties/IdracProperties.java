package com.wzm.fans.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "open-api")
public class IdracProperties {

    private String host;
    private String username;
    private String password;


}
