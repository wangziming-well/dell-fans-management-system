package com.wzm.fans.service;

import com.wzm.fans.api.RedfishApi;
import org.springframework.stereotype.Service;

@Service
public class RedfishService {
    private final RedfishApi api;

    public RedfishService(RedfishApi api) {
        this.api = api;
    }

    /**
     * 获取并返回cpu温度
     * @return cpu温度
     */
    public double cpuTemp(){
        return 0;
    }

}
