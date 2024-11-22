package com.wzm.fans.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wzm.fans.api.RedfishRequestException;
import com.wzm.fans.util.ConfigUtils;
import com.wzm.fans.util.FanSpeedCurve;
import com.wzm.fans.util.IpmiTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ScheduleService {

    private static final Log logger = LogFactory.getLog(ScheduleService.class);

    private final FanSpeedCurve cupFanCurve;

    private final SystemMetricsService systemMetricsService;

    public ScheduleService(SystemMetricsService systemMetricsService) {
        this.systemMetricsService = systemMetricsService;
        HashMap<Integer, Integer> cupPoints =ConfigUtils.get("curve-points.default", new TypeReference<>() {});
        this.cupFanCurve = new FanSpeedCurve(cupPoints);
    }

    private volatile int previousFanPwm =-1;


    //@Scheduled(fixedRate = 5000)
    public void pollApi() {
        try{
            int temp = getCpuTemp();
            int fanPwm = cupFanCurve.getFanSpeed(temp);
            if (previousFanPwm != fanPwm){
                //需要调整转速
                IpmiTool.setFansPWM(fanPwm);
                previousFanPwm = fanPwm;
                logger.info(String.format("当前cpu温度:%d，调整转速:%d",temp,fanPwm));
            } else{
                logger.info(String.format("当前cpu温度:%d，无需调整转速",temp));
            }
        } catch (RedfishRequestException e){
            logger.warn("Redfish连接错误，请检查idrac配置。errorMessage:"+ e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Integer getCpuTemp(){
        List<String> sensorNames = systemMetricsService.getSubItemNames(SystemMetricsService.Metrics.TEMPERATURE);
        int maxCpuTemp = Integer.MIN_VALUE;
        for (String sensorName : sensorNames) {
            if (sensorName.contains("CPU")){
                int temp = systemMetricsService.getLatestMetrics(SystemMetricsService.Metrics.TEMPERATURE,sensorName);
                maxCpuTemp = Math.max(maxCpuTemp, temp);
            }
        }
        return maxCpuTemp;
    }
}