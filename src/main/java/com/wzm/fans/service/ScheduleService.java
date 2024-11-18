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
        HashMap<Integer, Integer> cupPoints = new HashMap<>();
        cupPoints.put(0,10);
        cupPoints.put(40,10);
        cupPoints.put(65,30);
        cupPoints.put(80,40);
        cupPoints.put(100,100);
        this.cupFanCurve = new FanSpeedCurve(cupPoints);
        this.redfishService = redfishService;
    }


    // 每隔 polling.interval/1000 秒执行一次轮询任务
    @Scheduled(fixedRate = 5000)
    public void pollApi() {
        int temp = redfishService.cpuTemp();
        int fanPwm = cupFanCurve.getFanSpeed(temp);
        logger.info(String.format("当前cpu温度:%d，调整转速:%d",temp,fanPwm));
        ipmiService.setFansPWM(fanPwm);
    }
}