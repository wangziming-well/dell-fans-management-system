package com.wzm.fans.util;


import java.util.*;

public class FanSpeedCurve {

    // 存储温度-风扇转速的映射关系
    private TreeMap<Double, Integer> pointMap;

    public FanSpeedCurve(Map<Double,Integer> points) {
        this.pointMap = new TreeMap<>(points); // 使用 TreeMap 自动按温度排序
    }

    // 根据温度获取风扇转速
    public int getFanSpeed(double temperature) {
        if (pointMap.isEmpty()) {
            throw new IllegalStateException("No data points available.");
        }

        // 查找两个邻近点，并通过线性插值计算风扇转速
        Map.Entry<Double, Integer> lowerEntry = pointMap.lowerEntry(temperature);
        Map.Entry<Double, Integer> upperEntry = pointMap.higherEntry(temperature);

        if (lowerEntry == null || upperEntry == null) {
            throw new IllegalArgumentException("Temperature out of bounds.");
        }

        // 如果温度等于某个点的温度，直接返回该点的风扇转速
        if (lowerEntry.getKey() == temperature) {
            return lowerEntry.getValue();
        }

        // 线性插值
        double slope = (upperEntry.getValue() - lowerEntry.getValue()) / (upperEntry.getKey() - lowerEntry.getKey());
        double result = lowerEntry.getValue() + slope * (temperature - lowerEntry.getKey());
        return (int) result ;
    }

    public Map<Double,Integer> getPoints(){
        return  Collections.unmodifiableMap(this.pointMap);
    }

    public void updateCurve(Map<Double,Integer> points){
        this.pointMap = new TreeMap<>(points);
    }

}