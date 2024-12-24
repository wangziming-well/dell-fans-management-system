package com.wzm.fans.controller;

import com.wzm.fans.pojo.Response;
import com.wzm.fans.util.IpmiTool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ipmi")
public class IPMIController {

    @GetMapping("/set/{speed}")
    public Response<Void> setSpeed(@PathVariable Integer speed){
        IpmiTool.setFansPWM(speed);
        return Response.ok();
    }

}
