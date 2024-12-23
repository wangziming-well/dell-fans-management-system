package com.wzm.fans.controller;

import com.wzm.fans.pojo.AppConfig;
import com.wzm.fans.pojo.DataDomainResponse;
import com.wzm.fans.pojo.Response;
import com.wzm.fans.util.ConfigUtils;
import com.wzm.fans.util.DateUtils;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    @GetMapping("/dataDomain")
    public Response<List<DataDomainResponse>> dataDomain(){
        List<AppConfig.Tier> dataTiers = ConfigUtils.getAll().getDataTiers();
        List<DataDomainResponse> responseList = Stream.of("10m", "1h", "3h", "6h", "12h", "1d", "3d", "7d", "14d")
                .map(domainStr -> calculateDomain(dataTiers, domainStr))
                .filter(Objects::nonNull)
                .toList();
        return Response.ok(responseList);
    }

    private DataDomainResponse calculateDomain( List<AppConfig.Tier> dataTiers,String domainStr ){
        DataDomainResponse response = new DataDomainResponse();
        int domainSeconds = DateUtils.toSecond(domainStr);
        response.setDomainSeconds(domainSeconds);
        response.setDomainStr(domainStr);
        for (int i = 0; i < dataTiers.size(); i++) {
            AppConfig.Tier tier = dataTiers.get(i);
            Integer expire = tier.getExpire();
            Integer interval = tier.getInterval();
            if (domainSeconds <= expire){
                response.setTier(i+1);
                response.setIntervalSeconds(interval);
                response.setIntervalStr(DateUtils.fromSecond(interval));
                return response;
            }
        }
        return null;
    }
}
