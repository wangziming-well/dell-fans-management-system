package com.wzm.fans.pojo;

import lombok.Data;

import java.util.List;

@Data
public class AllDataRecordsResponse {

    private long timestamp;

    private List<Integer> valueList;

}
