package com.wzm.fans.service;

import com.wzm.fans.util.FanSpeedCurve;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiPollingService {

    FanSpeedCurve cupFanCurve;


    // 每隔 polling.interval/1000 秒执行一次轮询任务
    @Scheduled(fixedRateString = "${polling.interval}")
    public void pollApi() {

        double temperature = 10;


    }
}