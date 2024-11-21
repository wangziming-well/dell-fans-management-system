package com.wzm.fans.service;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.api.ThermalResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RedfishService {

    private final RedfishApi api;

    public RedfishService(RedfishApi api) {
        this.api = api;
    }


    public Map<String,Integer> tempInfo(){
        ThermalResponse thermalResponse = api.thermal();
        List<ThermalResponse.Temperature> temperatures = thermalResponse.getTemperatures();
        return temperatures.stream().collect(Collectors.
                toMap(ThermalResponse.Temperature::getName,
                        ThermalResponse.Temperature::getReadingCelsius));
    }

}
