package com.wzm.fans.service;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.api.model.PowerResponse;
import com.wzm.fans.api.model.ThermalResponse;
import com.wzm.fans.pojo.RedfishThermalInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RedfishService {

    private final RedfishApi api;

    public RedfishService(RedfishApi api) {
        this.api = api;
    }


    public RedfishThermalInfo thermalInfo(){
        ThermalResponse thermalResponse = api.thermal();// TODO 异常处理
        Assert.notNull(thermalResponse,"redfish thermal api返回空");

        List<ThermalResponse.Temperature> temperatures = thermalResponse.getTemperatures();
        Assert.notNull(temperatures,"temperatures为空");
        Map<String, Double> tempMap = temperatures.stream()
                .filter(temperature -> temperature != null && temperature.getName() != null)
                .collect(Collectors.toMap(temperature ->
                                temperature.getName()
                                        .replace("System Board", "")
                                        .replace("Temp", "").trim(),
                        ThermalResponse.Temperature::getReadingCelsius));
        List<ThermalResponse.Fan> fans = thermalResponse.getFans();
        Map<String, Double> fanMap = fans.stream().filter(fan -> fan != null && fan.getFanName() != null)
                .collect(Collectors.toMap(fan ->
                        fan.getFanName().replace("System Board","").trim(),
                        ThermalResponse.Fan::getReading));
        RedfishThermalInfo redfishThermalInfo = new RedfishThermalInfo();
        redfishThermalInfo.setTemp(tempMap);
        redfishThermalInfo.setFanSpeed(fanMap);
        return redfishThermalInfo;
    }

    public Map<String, Integer> cpuUsageInfo() {

        return null;
    }

    public Map<String, Double> powerConsumeInfo() {
        PowerResponse powerResponse = api.power();
        Assert.notNull(powerResponse,"redfish power api 返回空");
        List<PowerResponse.PowerControl> powerControls = powerResponse.getPowerControl();
        Assert.notNull(powerControls,"powerControl为空");
        return powerControls.stream().filter(powerControl -> powerControl!=null && powerControl.getName()!= null)
                .collect(Collectors.toMap(PowerResponse.PowerControl::getName, PowerResponse.PowerControl::getPowerConsumedWatts));

    }
}
