package com.wzm.fans.api;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
class RedFishApiEnhanceAspect {

    private final RedfishBaseUrlChanger redfishBaseUrlChanger;

    public RedFishApiEnhanceAspect(RedfishBaseUrlChanger redfishBaseUrlChanger) {
        this.redfishBaseUrlChanger = redfishBaseUrlChanger;
    }

    @Pointcut("execution(* com.wzm.fans.api.RedfishApi.changeHost(..))")
    public void changeHostMethod(){}

    @Pointcut("execution(* com.wzm.fans.api.RedfishApi.*(..))")
    public void allMethods(){}

    @Before("changeHostMethod()&& args(host)")
    public void aroundOriginalMethod(String host) {
        String baseUrl = RedfishApi.baseUrl(host);
        redfishBaseUrlChanger.changeBaseUrl(baseUrl);
    }

    @AfterThrowing(pointcut = "allMethods() && ! changeHostMethod()",throwing = "e")
    public void advice(Exception e){
        throw new RedfishRequestException( "Redfish请求错误:" +e.getMessage(),e);
    }
}