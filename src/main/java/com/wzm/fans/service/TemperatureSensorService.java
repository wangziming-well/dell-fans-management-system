package com.wzm.fans.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TemperatureSensorService {

    public TemperatureSensorService(RedfishService redfishService) {
        this.redfishService = redfishService;
    }

    private final RedfishService redfishService;

    private final ConcurrentSkipListMap<Long, Integer[]> tempRecordMap = new ConcurrentSkipListMap<>();

    private final List<String> sensorNames = new CopyOnWriteArrayList<>();

    @Scheduled(fixedRate = 1000)
    public void tempRecord() {
        Map<String, Integer> tempInfoMap = redfishService.tempInfo();
        long timestamp = System.currentTimeMillis();
        Integer[] tempList = getTempList(tempInfoMap);
        tempRecordMap.put(timestamp, tempList);

        // 清理超过1小时的旧记录（可根据需求调整时间）
        long oneHourAgo = timestamp - 3600_000;
        tempRecordMap.headMap(oneHourAgo).clear();
    }

    /**
     * 根据tempInfoMap获取 温度的列表
     * 传感器名称会按顺序记录到sensorNames，tempRecordMap的温度列表会按照传感器顺序排序
     * 当记录过程中新增了传感器，添加到sensorNames列表的最后
     * 当记录过程中减少了传感器，sensorNames列表不变。减少后，读取的温度为null
     *
     * @param tempInfoMap 传感器名称和温度的映射
     * @return 返回符合sensorNames 顺序的温度列表
     */
    private Integer[] getTempList(Map<String, Integer> tempInfoMap) {
        //遍历tempInfoMap的传感器名称，记录新传感器
        tempInfoMap.keySet().stream().sorted().forEach(sensorName -> {
            if (!this.sensorNames.contains(sensorName))
                this.sensorNames.add(sensorName);
        });
        //遍历this.sensorNames,按照顺序根据tempInfoMap信息获取温度列表
        return this.sensorNames
                .stream()
                .map(tempInfoMap::get)
                .toArray(Integer[]::new);
    }


    public List<String> getSensorNames() {
        return Collections.unmodifiableList(this.sensorNames);
    }

    public Map<Long, Integer> getTempRecords(String sensorName) {
        int index = indexOf(sensorName);
        HashMap<Long, Integer> map = new HashMap<>();
        this.tempRecordMap.forEach((timestamp, tempList) -> map.put(timestamp, tempList[index]));
        return map;
    }

    public Integer getLeastTemp(String sensorName) {
        int index = indexOf(sensorName);
        return this.tempRecordMap.lastEntry().getValue()[index];
    }

    private int indexOf(String sensorName) {
        int index = sensorNames.indexOf(sensorName);
        if (index == -1)
            throw new IllegalArgumentException("没有该传感器:" + sensorName);
        return index;
    }

}
