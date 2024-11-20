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
import org.springframework.web.reactive.function.client.WebClientRequestException;

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

    private volatile int previousFanPWn =-1;


    @Scheduled(fixedRate = 5000)
    public void pollApi() {
        try{
            boolean ipmitoolAvailable = IpmiTool.isIpmitoolAvailable();
            System.out.println(ipmitoolAvailable);
            int temp = redfishService.cpuTemp();
            int fanPwm = cupFanCurve.getFanSpeed(temp);
            if (previousFanPWn != fanPwm){
                //需要调整转速
                IpmiTool.setFansPWM(fanPwm);
                previousFanPWn = fanPwm;
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
}