package com.wzm.fans.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wzm.fans.api.RedfishRequestException;
import com.wzm.fans.pojo.AppConfig;
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
        List<AppConfig.CurvePoint> curvePoints = ConfigUtils.getAll().getCurvePoints();
        AppConfig.CurvePoint selectCurvePoint = null;
        for (int i = 0; i < curvePoints.size(); i++) {
            AppConfig.CurvePoint curvePoint = curvePoints.get(i);
            if (curvePoint.getName().equals("MAX")){
                selectCurvePoint = curvePoint;
            }
        }
        if (selectCurvePoint == null)
            throw new RuntimeException("不存在MAX配置");
        Map<Double, Double> resultCurve = selectCurvePoint.getCurve().entrySet().stream()
                .collect(Collectors.toMap(entry -> Double.parseDouble(entry.getKey()),
                        entry -> entry.getValue().doubleValue()));
        this.cupFanCurve = new FanSpeedCurve(resultCurve);

    }


    private volatile double previousCpuTemp =-1;

    @Scheduled(fixedRate = 5000)
    public void pollApi() {
        try{
            if (!systemMetricsService.isReady())
                return;
            Map<String, Double> sensorTemps = getSensorTemps();
            if (sensorTemps.isEmpty())
                return;
            Double currMaxTemp = sensorTemps.values().stream()
                    .reduce(BinaryOperator.maxBy(Double::compareTo)).get();
            int fanPwm = cupFanCurve.getFanSpeed(currMaxTemp);
            StringBuilder sb = new StringBuilder("当前温度:");
            sensorTemps.forEach((key, value) -> sb.append(String.format("[%s:%.0f] ", key, value)));
            sb.append(String.format("[%s:%.0f] ","Max", currMaxTemp));
            if (previousCpuTemp != currMaxTemp){
                //可能需要调整转速
                IpmiTool.setFansPWM(fanPwm);
                previousCpuTemp = currMaxTemp;
                sb.append(String.format("调整转速:%d",fanPwm));
                logger.info(sb);
            }
        } catch (RedfishRequestException e){
            logger.warn("Redfish连接错误，请检查idrac配置。errorMessage:"+ e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Map<String,Double> getSensorTemps(){
        List<DataItem<Double>> latest = systemMetricsService.getLatest(SystemMetricsService.Category.TEMPERATURE);
        return  latest.stream().collect(Collectors.toMap(DataItem::getName, DataItem::getValue));
    }
}