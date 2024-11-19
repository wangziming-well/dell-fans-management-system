package com.wzm.fans.service;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.api.ThermalResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedfishService {

    private final RedfishApi api;

    public RedfishService(RedfishApi api) {
        this.api = api;
    }

    /**
     * 获取并返回cpu温度
     * @return cpu温度
     */
    public int cpuTemp(){
        ThermalResponse thermalResponse = api.thermal();
        List<ThermalResponse.Temperature> temperatures = thermalResponse.getTemperatures();
        int maxTemp = Integer.MIN_VALUE;

        for (ThermalResponse.Temperature temperature : temperatures) {
            String name = temperature.getName();
            Integer temp = temperature.getReadingCelsius();
            if (name.contains("CPU"))
                maxTemp = maxTemp < temp ? temp : maxTemp;
        }
        return maxTemp;
    }

}
