package com.wzm.fans.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class RedfishThermalInfo {
    private Map<String,Double> temp;

    private Map<String,Double> fanSpeed;

}
