package com.wzm.fans;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.service.ConfigService;
import com.wzm.fans.service.RedfishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DellFansManagementSystemApplicationTests {


    @Autowired
    ConfigService configService;

    @Test
    void contextLoads() {
        String s = configService.get("idrac.host");
        System.out.println(s);
    }

}
