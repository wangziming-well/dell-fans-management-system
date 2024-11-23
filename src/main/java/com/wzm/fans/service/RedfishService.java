package com.wzm.fans.service;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.api.model.ThermalResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RedfishService {

    private final RedfishApi api;

    public RedfishService(RedfishApi api) {
        this.api = api;
    }


    public Map<String,Integer> tempInfo(){
        ThermalResponse thermalResponse = api.thermal();
        Assert.notNull(thermalResponse,"redfish api返回空");
        List<ThermalResponse.Temperature> temperatures = thermalResponse.getTemperatures();
        Assert.notNull(temperatures,"temperatures为");

        return temperatures.stream().filter(Objects::nonNull).collect(Collectors.
                toMap(ThermalResponse.Temperature::getName,
                        ThermalResponse.Temperature::getReadingCelsius));
    }

    public Map<String, Integer> cpuUsageInfo() {

        return null;
    }

    public Map<String, Integer> powerConsumeInfo() {
        return null;
    }

    public Map<String, Integer> fanSpeedInfo() {
        return null;
    }
}
