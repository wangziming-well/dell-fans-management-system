package com.wzm.fans.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
    @AllArgsConstructor
    public class DataItem<T> {
        private final long timestamp;
        private final String name;
        private final T value;
    }