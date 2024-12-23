package com.wzm.fans.pojo;

import lombok.Data;

@Data
public class DataDomainResponse {

    private int domainSeconds;

    private String domainStr;

    private int tier;

    private int intervalSeconds;

    private String intervalStr;

}
