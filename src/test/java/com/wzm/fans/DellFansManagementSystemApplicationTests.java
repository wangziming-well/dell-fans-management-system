package com.wzm.fans;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.api.model.PowerResponse;
import com.wzm.fans.service.RedfishService;
import com.wzm.fans.util.IpmiTool;
import com.wzm.fans.util.JacksonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class DellFansManagementSystemApplicationTests {



    @Autowired
    RedfishApi api;

    @Autowired
    RedfishService redfishService;

    @Test
    void contextLoads() {
        PowerResponse power = api.power();
        System.out.println(JacksonUtils.toJsonString(power));
    }

}
