package com.wzm.fans.api;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface RedfishApi {



    @GetExchange
    String get();


}
