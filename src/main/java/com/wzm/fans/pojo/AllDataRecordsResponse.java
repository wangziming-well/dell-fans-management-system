package com.wzm.fans.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllDataRecordsResponse {

    private long timestamp;

    private List<Double> valueList;

}
