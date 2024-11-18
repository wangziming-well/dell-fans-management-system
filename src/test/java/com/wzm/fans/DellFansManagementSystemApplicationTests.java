package com.wzm.fans;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.api.ThermalResponse;
import com.wzm.fans.service.IpmiService;
import com.wzm.fans.service.RedfishService;
import com.wzm.fans.util.ConfigUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DellFansManagementSystemApplicationTests {

    @Autowired
    IpmiService ipmiService;

    @Autowired
    RedfishApi api;

    @Autowired
    RedfishService redfishService;

    @Test
    void contextLoads() {
        double v = redfishService.cpuTemp();
        System.out.println(v);
    }

}
