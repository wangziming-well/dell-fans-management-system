package com.wzm.fans;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.util.IpmiTool;
import com.wzm.fans.service.RedfishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DellFansManagementSystemApplicationTests {



    @Autowired
    RedfishApi api;

    @Autowired
    RedfishService redfishService;

    @Test
    void contextLoads() {
    }

}
