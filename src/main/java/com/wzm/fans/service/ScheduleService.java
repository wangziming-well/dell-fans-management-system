package com.wzm.fans.service;

import com.wzm.fans.util.FanSpeedCurve;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ScheduleService {

    private static final Log logger = LogFactory.getLog(IpmiService.class);

    private FanSpeedCurve cupFanCurve;

    private final RedfishService redfishService;

    private final IpmiService ipmiService;

    public ScheduleService(RedfishService redfishService, IpmiService ipmiService) {
        this.ipmiService = ipmiService;
        HashMap<Double, Integer> cupPoints = new HashMap<>();
        this.cupFanCurve = new FanSpeedCurve(cupPoints);
        this.redfishService = redfishService;
    }


    // 每隔 polling.interval/1000 秒执行一次轮询任务
    @Scheduled(fixedRateString = "${polling.interval}")
    public void pollApi() {
        double temp = redfishService.cpuTemp();
        int fanPwm = cupFanCurve.getFanSpeed(temp);
        logger.info(String.format("当前cpu温度:%f，调整转速:%d",temp,fanPwm));
        ipmiService.setFansPWM(fanPwm);
    }
}