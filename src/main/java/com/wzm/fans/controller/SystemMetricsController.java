package com.wzm.fans.controller;

import com.wzm.fans.pojo.Response;
import com.wzm.fans.service.SystemMetricsService;
import org.apache.commons.logging.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController("/metrics")
public class SystemMetricsController {

    private final SystemMetricsService service;

    public SystemMetricsController(SystemMetricsService service) {
        this.service = service;
    }

    @GetMapping("/temperature/itemNames")
    public Response<List<String>> temperatureItemNames(){
        List<String> subItemNames = service.getSubItemNames(SystemMetricsService.Metrics.TEMPERATURE);
        return Response.ok(subItemNames);
    }

    @GetMapping("/temperature/item/{itemName}")
    public Response<Map<Long,Integer>> temperatureInfo(@PathVariable String itemName){
        Map<Long, Integer> result = service.getRecords(SystemMetricsService.Metrics.TEMPERATURE, itemName);
        return Response.ok(result);
    }


}
