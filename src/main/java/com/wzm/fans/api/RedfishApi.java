package com.wzm.fans.api;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface RedfishApi {

    @GetExchange("/Chassis/System.Embedded.1")
    String get();

    @GetExchange
    String test();

    //由RedFishApiEnhanceAspect 拦截增强
    default void changeHost(String host){}

    String baseUrlTemplate = "https://%s/redfish/v1";

    static String baseUrl(String host){
        return String.format(baseUrlTemplate,host) ;
    }
}
