package com.wzm.fans.controller;

import com.wzm.fans.pojo.AllDataRecordsResponse;
import com.wzm.fans.pojo.DataItem;
import com.wzm.fans.pojo.DataRecordsResponse;
import com.wzm.fans.pojo.Response;
import com.wzm.fans.service.SystemMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/metrics")
public class SystemMetricsController {

    private final SystemMetricsService service;

    public SystemMetricsController(SystemMetricsService service) {
        this.service = service;
    }

    @GetMapping("/temperature/all/{tier}")
    public List<DataItem<Double>> allTemperatureInfo(@PathVariable Integer tier){
        List<DataItem<Double>> data = service.getData(tier);

        return service.getData(tier);
    }

    @GetMapping("/temperature/itemNames")
    public Response<List<String>>  allItemNames(){
        return Response.ok(service.getKeyNames()) ;
    }
}
