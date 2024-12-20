package com.wzm.fans.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 监控服务器各项指标的服务，通过{@link #recordMetrics()} 方法每秒记录服务器指标数据并保存，以提供给其他服务使用
 */
@Service
public class SystemMetricsService {

    public SystemMetricsService(RedfishService redfishService) {
        this.redfishService = redfishService;
    }

    public enum Metrics{
        TEMPERATURE,
        CPU_USAGE,
        POWER_CONSUME,
        FAN_SPEED
    }

    private MetricsRecorder<Integer> getRecorder(Metrics metrics){
        switch (metrics){
            case TEMPERATURE ->{
                return this.temperatureRecorder;
            }
            case CPU_USAGE -> {
                return this.cpuUsageRecorder;
            }
            case POWER_CONSUME ->{
                return this.powerConsumeRecorder;
            }
            case FAN_SPEED -> {
                return this.fanSpeedRecorder;
            }
            default -> throw new IllegalArgumentException("非法枚举" +metrics );
        }
    }

    private final RedfishService redfishService;

    private final MetricsRecorder<Integer>  temperatureRecorder = new MetricsRecorder<>(60*60*24*7);

    private final MetricsRecorder<Integer> cpuUsageRecorder = new MetricsRecorder<>(3600);

    private final MetricsRecorder<Integer> powerConsumeRecorder = new MetricsRecorder<>(3600);

    private final MetricsRecorder<Integer> fanSpeedRecorder = new MetricsRecorder<>(3600);

    @Scheduled(fixedRate = 1000)
    public void recordMetrics() {
        Map<String, Integer> tempInfoMap = redfishService.tempInfo();
        Map<String, Integer> cpuUsageInfoMap = redfishService.cpuUsageInfo();
        Map<String, Integer> powerConsumeInfoMap = redfishService.powerConsumeInfo();
        Map<String, Integer> fanSpeedInfoMap = redfishService.fanSpeedInfo();
        temperatureRecorder.records(tempInfoMap);
//        cpuUsageRecorder.records(cpuUsageInfoMap);
//        powerConsumeRecorder.records(powerConsumeInfoMap);
//        fanSpeedRecorder.records(fanSpeedInfoMap);
    }

    public List<String> getSubItemNames(Metrics metrics) {
        return getRecorder(metrics).getSubItemNames();
    }

    public Map<Long, Integer> getRecords(Metrics metrics, String subItemName) {
        return getRecorder(metrics).getRecords(subItemName);
    }

    public Map<Long, List<Integer>> getAllRecords(Metrics metrics){
        return getRecorder(metrics).getAllRecords();
    }

    public Integer getLatestMetrics(Metrics metrics, String subItemName) {
        return getRecorder(metrics).getLeastMetrics(subItemName);
    }

}
