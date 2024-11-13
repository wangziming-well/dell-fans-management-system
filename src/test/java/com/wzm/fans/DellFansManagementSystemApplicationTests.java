package com.wzm.fans;

import com.wzm.fans.api.OpenApiConfiguration;
import com.wzm.fans.api.RedfishApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DellFansManagementSystemApplicationTests {

    @Autowired
    private OpenApiConfiguration openApiConfiguration;
    @Autowired
    RedfishApi redfishApi;

    @Test
    void contextLoads() {

        String s = redfishApi.get();
        System.out.println(s);
        String s1 = redfishApi.get();
        System.out.println(s1);
    }

}
