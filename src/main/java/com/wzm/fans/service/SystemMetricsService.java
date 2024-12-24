package com.wzm.fans.service;

import com.wzm.fans.pojo.DataItem;
import com.wzm.fans.pojo.RedfishThermalInfo;
import com.wzm.fans.util.ConfigUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.wzm.fans.service.SystemMetricsService.Category.FAN_SPEED;
import static com.wzm.fans.service.SystemMetricsService.Category.TEMPERATURE;

/**
 * 监控服务器各项指标的服务，通过{@link DataRecorder} 方法分层记录服务器指标数据，以提供给其他服务使用
 */
@Service
public class SystemMetricsService {

    public enum Category {
        TEMPERATURE,
        FAN_SPEED
    }

    private final DataRecorder dataRecorder;

    private final RedfishService redfishService;

    @Getter
    private volatile boolean ready = false;

    public SystemMetricsService(RedfishService redfishService) {
        this.redfishService = redfishService;
        this.dataRecorder = initDataRecorder();
    }

    public DataRecorder initDataRecorder(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        List<DataRecorder.StorageTierStrategy> list =
                ConfigUtils.getAll().getDataTiers().stream()
                        .map(dataTier -> new DataRecorder.StorageTierStrategy(dataTier.getInterval(), dataTier.getExpire())).toList();
        DataRecorder dataRecorder = new DataRecorder(scheduledExecutorService, this::dataRequest,list);
        dataRecorder.start();
        scheduledExecutorService.schedule(() ->this.ready = true,1, TimeUnit.SECONDS);
        return dataRecorder;
    }

    public DataRecorder.DataContainer dataRequest() {
        RedfishThermalInfo redfishThermalInfo = redfishService.thermalInfo();
        DataRecorder.DataContainer result = new DataRecorder.DataContainer();
        result.add(TEMPERATURE.name(),redfishThermalInfo.getTemp());
        result.add(FAN_SPEED.name(),redfishThermalInfo.getFanSpeed());
        return result;
    }

    public List<DataItem<Double>> getData(Category category, int tier){
        return this.dataRecorder.getData(category.name(),tier);
    }

    public Map<Long,List<Double>> getStoreMap(Category category, int tier){
        return this.dataRecorder.getStoreMap(category.name() ,tier);
    }

    public List<DataItem<Double>> getLatest(Category category){
        return this.dataRecorder.getLatest(category.name());
    }


    public List<String> getKeyNames(Category category) {
        return this.dataRecorder.getKeys(category.name());
    }
}
