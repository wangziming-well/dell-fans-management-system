package com.wzm.fans.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wzm.fans.util.TimeStringFormatter;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppConfig {

    private Idrac idrac;

    @JsonProperty("curve-points")
    private List<CurvePoint> curvePoints;

    private Shutdown shutdown;
    @JsonProperty("data-tier")
    private List<Tier> dataTiers;

    @Data
    public static class CurvePoint{
        private String name;
        private Map<String,Integer> curve;

    }

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
        @JsonProperty("operate-fan")
        private boolean operateFan;
    }

    @Data
    public static class Tier{
        @JsonDeserialize(using = TimeStringFormatter.Deserializer.class)
        @JsonSerialize(using = TimeStringFormatter.Serializer.class)
        private Integer interval;
        @JsonDeserialize(using = TimeStringFormatter.Deserializer.class)
        @JsonSerialize(using = TimeStringFormatter.Serializer.class)
        private Integer expire;
    }
}