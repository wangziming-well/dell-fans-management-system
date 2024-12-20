package com.wzm.fans.controller;

import com.wzm.fans.pojo.AllDataRecordsResponse;
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

    @GetMapping("/temperature/itemNames")
    public Response<List<String>> temperatureItemNames(){
        List<String> subItemNames = service.getSubItemNames(SystemMetricsService.Metrics.TEMPERATURE);
        return Response.ok(subItemNames);
    }

    @GetMapping("/temperature/item/{itemName}.json")
    public List<DataRecordsResponse> temperatureInfo(@PathVariable String itemName){
        Map<Long, Integer> result = service.getRecords(SystemMetricsService.Metrics.TEMPERATURE, itemName);
        return result.entrySet()
                .stream()
                .map(entry -> new DataRecordsResponse(entry.getKey(), entry.getValue()))
                .sorted((d1,d2) -> Math.toIntExact(d1.getTimestamp() - d2.getTimestamp()))
                .toList();
    }

    @GetMapping("/temperature/all.json")
    public List<AllDataRecordsResponse> allTemperatureInfo(){
        Map<Long, List<Integer>> allRecords = service.getAllRecords(SystemMetricsService.Metrics.TEMPERATURE);
        List<String> subItemNames = service.getSubItemNames(SystemMetricsService.Metrics.TEMPERATURE);
        List<String> finalSubItemNames = subItemNames.stream().map( s -> s.replace("System Board","").trim()).toList();

        return  allRecords.entrySet().stream()
                .sorted((d1,d2) -> Math.toIntExact(d1.getKey() - d2.getKey()))
                .flatMap(entry -> {
                    long timestamp  = entry.getKey();
                    List<Integer> valueList = entry.getValue();
                    ArrayList<AllDataRecordsResponse> result = new ArrayList<>();
                    // 将数据映射到流中
                    return IntStream.range(0, finalSubItemNames.size())
                            .filter(i -> i < valueList.size()) // 确保 i 不超过 valueList 的大小
                            .mapToObj(i -> {
                                AllDataRecordsResponse item = new AllDataRecordsResponse();
                                item.setTimestamp(timestamp);
                                item.setName(finalSubItemNames.get(i));
                                item.setValue(valueList.get(i));
                                return item;
                            });
                }).toList();
    }
}
