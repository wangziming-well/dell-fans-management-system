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
import java.util.Map;
import java.util.function.BinaryOperator;

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


    private volatile int previousCpuTemp =-1;
    @Scheduled(fixedRate = 5000)
    public void pollApi() {
        try{
            Map<String, Integer> sensorTemps = getSensorTemps();
            Integer currMaxTemp = sensorTemps.values().stream()
                    .reduce(BinaryOperator.maxBy(Integer::compareTo)).get();
            int fanPwm = cupFanCurve.getFanSpeed(currMaxTemp);
            StringBuilder sb = new StringBuilder("当前温度:");
            sensorTemps.entrySet().stream().forEach(entry-> sb.append(String.format("[%s:%d] ",entry.getKey(),entry.getValue())));
            sb.append(String.format("[%s:%d] ","Max",currMaxTemp));
            if (previousCpuTemp != currMaxTemp){
                //可能需要调整转速
                IpmiTool.setFansPWM(fanPwm);
                previousCpuTemp = currMaxTemp;
                sb.append(String.format("调整转速:%d",fanPwm));
                logger.info(sb);
            } else{
                if (!ConfigUtils.getBoolean("log.less"))
                    logger.info(sb);
            }
        } catch (RedfishRequestException e){
            logger.warn("Redfish连接错误，请检查idrac配置。errorMessage:"+ e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Map<String,Integer> getSensorTemps(){
        List<String> sensorNames = systemMetricsService.getSubItemNames(SystemMetricsService.Metrics.TEMPERATURE);
        HashMap<String, Integer> result = new HashMap<>();
        for (String sensorName : sensorNames) {
            int temp = systemMetricsService.getLatestMetrics(SystemMetricsService.Metrics.TEMPERATURE,
                    sensorName);
            result.put(sensorName.replace("System Board","").replace("Temp","").trim(),temp);

        }
        return result;
    }
}