package com.wzm.fans.pojo;

import lombok.Data;

@Data
public class DataDomainResponse {

    private int domain; //单位 秒


    private int tier;

    private int interval; //单位 秒


}
