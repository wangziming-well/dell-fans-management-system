package com.wzm.fans.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface RedfishApi {

    @GetExchange("/Sessions")
    SessionsResponse sessions(@RequestBody SessionsRequest request);

    @GetExchange("/Chassis/System.Embedded.1")
    String get();

}
