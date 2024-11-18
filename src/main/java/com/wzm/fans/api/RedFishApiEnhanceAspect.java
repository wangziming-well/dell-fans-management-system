package com.wzm.fans.api;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
class RedFishApiEnhanceAspect {

    private final RedfishBaseUrlChanger redfishBaseUrlChanger;

    public RedFishApiEnhanceAspect(RedfishBaseUrlChanger redfishBaseUrlChanger) {
            this.redfishBaseUrlChanger = redfishBaseUrlChanger;
        }

        @Before("execution(* com.wzm.fans.api.RedfishApi.changeHost(..))&& args(host)")
        public void aroundOriginalMethod(String host){
            String baseUrl = RedfishApi.baseUrl(host);
            redfishBaseUrlChanger.changeBaseUrl(baseUrl);
    }
}