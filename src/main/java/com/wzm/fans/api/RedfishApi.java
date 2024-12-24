package com.wzm.fans.api;


import com.wzm.fans.api.model.PowerResponse;
import com.wzm.fans.api.model.ThermalResponse;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface RedfishApi {

    @GetExchange("/Chassis/System.Embedded.1/Thermal")
    ThermalResponse thermal();

    @GetExchange("/Chassis/System.Embedded.1/Power")
    PowerResponse power();

    //由RedFishApiEnhanceAspect 拦截增强
    default void changeHost(String host){}

    String baseUrlTemplate = "https://%s/redfish/v1";

    static String baseUrl(String host){
        return String.format(baseUrlTemplate,host) ;
    }
}
