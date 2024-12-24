package com.wzm.fans.controller;

import com.wzm.fans.pojo.DataResponse;
import com.wzm.fans.pojo.Response;
import com.wzm.fans.service.SystemMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metrics")
public class SystemMetricsController {

    private final SystemMetricsService service;

    public SystemMetricsController(SystemMetricsService service) {
        this.service = service;
    }

    @GetMapping("/temperature/{tier}/data")
    public List<DataResponse> temperatureData(@PathVariable Integer tier){
        Map<Long, List<Double>> storeMap = service.getStoreMap(SystemMetricsService.Category.TEMPERATURE,tier);

        return storeMap.entrySet().stream()
                .map(entry -> new DataResponse(entry.getKey(),entry.getValue()))
                .toList();
    }

    @GetMapping("/temperature/keyNames")
    public Response<List<String>>  temperatureKeyNames(){
        return Response.ok(service.getKeyNames(SystemMetricsService.Category.TEMPERATURE)) ;
    }

    @GetMapping("/fan/{tier}/data")
    public List<DataResponse> fanData(@PathVariable Integer tier){
        Map<Long, List<Double>> storeMap = service.getStoreMap(SystemMetricsService.Category.FAN_SPEED,tier);
        return storeMap.entrySet().stream()
                .map(entry -> new DataResponse(entry.getKey(),entry.getValue()))
                .toList();
    }

    @GetMapping("/fan/keyNames")
    public Response<List<String>>  fanKeyNames(){
        return Response.ok(service.getKeyNames(SystemMetricsService.Category.FAN_SPEED)) ;
    }


    @GetMapping("/power/{tier}/data")
    public List<DataResponse> powerData(@PathVariable Integer tier){
        Map<Long, List<Double>> storeMap = service.getStoreMap(SystemMetricsService.Category.POWER_CONSUMPTION,tier);
        return storeMap.entrySet().stream()
                .map(entry -> new DataResponse(entry.getKey(),entry.getValue()))
                .toList();
    }

    @GetMapping("/power/keyNames")
    public Response<List<String>>  powerKeyNames(){
        return Response.ok(service.getKeyNames(SystemMetricsService.Category.POWER_CONSUMPTION)) ;
    }
}
