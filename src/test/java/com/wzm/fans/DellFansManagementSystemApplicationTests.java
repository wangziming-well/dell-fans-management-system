package com.wzm.fans;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.service.RedfishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DellFansManagementSystemApplicationTests {


    @Autowired
    RedfishService service;

    @Autowired
    RedfishApi api;

    @Test
    void contextLoads() {
        String s = api.get();
        System.out.println(s);
    }

}
