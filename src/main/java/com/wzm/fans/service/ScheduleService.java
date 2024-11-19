package com.wzm.fans.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wzm.fans.util.ConfigUtils;
import com.wzm.fans.util.FanSpeedCurve;
import com.wzm.fans.util.IpmiTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ScheduleService {

    private static final Log logger = LogFactory.getLog(ScheduleService.class);

    private final FanSpeedCurve cupFanCurve;

    private final RedfishService redfishService;


    public ScheduleService(RedfishService redfishService) {
        HashMap<Integer, Integer> cupPoints =ConfigUtils.get("curve-points", new TypeReference<>() {});

        this.cupFanCurve = new FanSpeedCurve(cupPoints);
        this.redfishService = redfishService;
    }


    @Scheduled(fixedRate = 5000)
    public void pollApi() {
        int temp = redfishService.cpuTemp();
        int fanPwm = cupFanCurve.getFanSpeed(temp);
        IpmiTool.setFansPWM(fanPwm);
        logger.info(String.format("当前cpu温度:%d，调整转速:%d",temp,fanPwm));

    }
}