package com.wzm.fans.controller;

import com.wzm.fans.pojo.AppConfig;
import com.wzm.fans.pojo.Response;
import com.wzm.fans.util.ConfigUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/config")
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
