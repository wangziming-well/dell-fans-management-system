package com.wzm.fans.controller;

import com.wzm.fans.pojo.AppConfig;
import com.wzm.fans.pojo.Response;
import com.wzm.fans.util.ConfigUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/config")
public class ConfigController {
    @GetMapping
    public Response<AppConfig> get(){
        return Response.ok(ConfigUtils.getAll());
    }

    @PostMapping
    public Response<Void> set(@RequestBody AppConfig configMap){
        ConfigUtils.setAll(configMap);
        return Response.ok();
    }
}
