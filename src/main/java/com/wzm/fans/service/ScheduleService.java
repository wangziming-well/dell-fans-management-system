package com.wzm.fans.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wzm.fans.api.RedfishRequestException;
import com.wzm.fans.pojo.DataItem;
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
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private static final Log logger = LogFactory.getLog(ScheduleService.class);

    private final FanSpeedCurve cupFanCurve;

    private final SystemMetricsService systemMetricsService;

    public ScheduleService(SystemMetricsService systemMetricsService) {
        this.systemMetricsService = systemMetricsService;
        HashMap<Double, Double> cupPoints =ConfigUtils.get("curve-points.default", new TypeReference<>() {});
        this.cupFanCurve = new FanSpeedCurve(cupPoints);
    }


    private volatile double previousCpuTemp =-1;
    @Scheduled(fixedRate = 5000)
    public void pollApi() {
        try{
            Map<String, Double> sensorTemps = getSensorTemps();
            if (sensorTemps.size() <=0)
                return;
            Double currMaxTemp = sensorTemps.values().stream()
                    .reduce(BinaryOperator.maxBy(Double::compareTo)).get();
            double fanPwm = cupFanCurve.getFanSpeed(currMaxTemp);
            StringBuilder sb = new StringBuilder("当前温度:");
            sensorTemps.forEach((key, value) -> sb.append(String.format("[%s:%f] ", key, value)));
            sb.append(String.format("[%s:%f] ","Max",currMaxTemp));
            if (previousCpuTemp != currMaxTemp){
                //可能需要调整转速
                IpmiTool.setFansPWM(fanPwm);
                previousCpuTemp = currMaxTemp;
                sb.append(String.format("调整转速:%f",fanPwm));
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

    private Map<String,Double> getSensorTemps(){
        List<DataItem<Double>> latest = systemMetricsService.getLatest();
        return  latest.stream().collect(Collectors.toMap(DataItem::getName, DataItem::getValue));
    }
}