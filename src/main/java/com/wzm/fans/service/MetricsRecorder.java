package com.wzm.fans.service;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * 提供服务器各项指标的存储和查询服务
 * 如温度指标，能耗指标等。
 *
 * @param <T> 为指标的子项值类型
 */

public class MetricsRecorder<T> {
    /**
     * 时间戳-子项指标数组的map
     * 其中子项指标数组的值的顺序为 {@link #subItemNames} 定义的顺序
     */
    private final ConcurrentSkipListMap<Long, T[]> metricsRecordMap = new ConcurrentSkipListMap<>();
    // 指标的子项名称，如温度指标中可能有cpu温度，进风口温度等
    private final List<String> subItemNames = new CopyOnWriteArrayList<>();

    private final int retentionPeriod;

    /**
     * @param retentionPeriod 记录的有效时长，超过该时长的记录会被删除，单位为s
     */
    public MetricsRecorder(int retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    /**
     * 记录指定的数据
     *
     * @param metricsMap 指标的子项名称-子项值 的键值对
     */
    public void records(Map<String, T> metricsMap) {
        T[] tempList = toMetricsArray(metricsMap);
        long timestamp = System.currentTimeMillis();
        metricsRecordMap.put(timestamp, tempList);
        // TODO 处理旧数据，和新增时间的压缩归档 和持久化
        long oneHourAgo = timestamp - retentionPeriod * 1000L;
        metricsRecordMap.headMap(oneHourAgo).clear();
    }

    /**
     * 获取记录的指标的所有子项名称
     * @return 返回记录的指标的所有子项名称
     */
    public List<String> getSubItemNames() {
        return Collections.unmodifiableList(this.subItemNames);
    }

    public Map<Long, T> getRecords(String subItemName) {
        int index = indexOf(subItemName);
        HashMap<Long, T> map = new HashMap<>();
        this.metricsRecordMap.forEach((timestamp, tempList)
                -> {
            if (index < tempList.length)
                map.put(timestamp, tempList[index]);
        });
        return map;
    }

    public Map<Long, List<T>> getAllRecords(){
        HashMap<Long, List<T>> result = new HashMap<>();
        metricsRecordMap.forEach((key, value) -> {
            List<T> list = Arrays.stream(value).toList();
            result.put(key, list);
        });
        return result;
    }

    public T getLeastMetrics(String subItemName) {
        if (this.metricsRecordMap.isEmpty())
            throw new IllegalArgumentException("当前记录为空");
        int index = indexOf(subItemName);
        return this.metricsRecordMap.lastEntry().getValue()[index];
    }

    /**
     * 根据 {@code metricsMap}获取 指标的各子项的具体数据的列表 <br/>
     * {@code metricsMap}是 子项名称-子项值 的键值对 <br/>
     * 子项名称会按顺序记录到{@link #subItemNames} <br/>
     * 当记录过程中{@code metricsMap} 有新的子项，该子项名称会添加到{@link #subItemNames}列表的最后
     * 当记录过程中{@code metricsMap} 有子项的缺失，{@link #subItemNames}列表不变，缺失的子项值在返回的列表中值为{@code null}
     *
     * @param metricsMap 指标的子项名称-子项值 的键值对
     * @return 返回符合 {@link #subItemNames} 顺序的子项值列表
     */
    private T[] toMetricsArray(Map<String, T> metricsMap) {
        //遍历tempInfoMap的传感器名称，记录新传感器
        synchronized (this){
            metricsMap.keySet().stream().sorted().forEach(sensorName -> {
                if (!this.subItemNames.contains(sensorName))
                    this.subItemNames.add(sensorName);
            });
        }
        //遍历this.sensorNames,按照顺序根据tempInfoMap信息获取温度列表
        Stream<T> stream = this.subItemNames
                .stream()
                .map(metricsMap::get);
        return convertStreamToArray(stream);
    }

    @SuppressWarnings("unchecked")
    private T[] convertStreamToArray(Stream<T> stream) {
        return (T[]) stream.toArray(Object[]::new);
    }

    private int indexOf(String subItemName) {
        int index = subItemNames.indexOf(subItemName);
        if (index == -1)
            throw new IllegalArgumentException("没有该传感器:" + subItemName);
        return index;
    }
}
