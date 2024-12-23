package com.wzm.fans.service;

import com.wzm.fans.pojo.DataItem;
import com.wzm.fans.util.ConfigUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * 监控服务器各项指标的服务，通过{@link DataRecorder} 方法分层记录服务器指标数据，以提供给其他服务使用
 */
@Service
public class SystemMetricsService {

    private final DataRecorder dataRecorder;

    private final RedfishService redfishService;

    public SystemMetricsService(RedfishService redfishService) {
        this.redfishService = redfishService;
        this.dataRecorder = initDataRecorder();
    }

    public DataRecorder initDataRecorder(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        List<DataRecorder.StorageTierStrategy> list =
                ConfigUtils.getAll().getDataTiers().stream()
                        .map(dataTier -> new DataRecorder.StorageTierStrategy(dataTier.getInterval(), dataTier.getExpire())).toList();
        DataRecorder dataRecorder = new DataRecorder(scheduledExecutorService, this::dataRequest,list);
        dataRecorder.start();
        return dataRecorder;
    }

    public Map<String, Double> dataRequest() {
        Map<String, Integer> tempInfoMap = redfishService.tempInfo();
        return tempInfoMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().doubleValue()
                ));
    }

    public List<DataItem<Double>> getData(int tier){
        return this.dataRecorder.getData(tier);
    }

    public Map<Long,List<Double>> getStoreMap(int tier){
        return this.dataRecorder.getStoreMap(tier);
    }

    public List<DataItem<Double>> getLatest(){
        return this.dataRecorder.getLatest();
    }


    public List<String> getKeyNames() {
        return this.dataRecorder.getKeys();
    }
}
