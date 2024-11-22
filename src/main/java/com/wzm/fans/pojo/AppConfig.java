package com.wzm.fans.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AppConfig {

    private Idrac idrac;

    @JsonProperty("curve-points")
    private Map<String, Map<String, Integer>> curvePoints;

    private Shutdown shutdown;

    @Data
    public static class Idrac {
        private String host;
        private String username;
        private String password;
    }

    @Data
    public static class Shutdown {
        @JsonProperty("auto-mode")
        private boolean autoMode;

        @JsonProperty("fans-speed")
        private int fansSpeed;
    }
}