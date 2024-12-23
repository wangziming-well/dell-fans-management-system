package com.wzm.fans.service;

import com.wzm.fans.pojo.DataItem;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * 根据时间戳存储数据的内存数据仓库
 * 需要存储：时间戳、和对应的键值数组
 */
public class DataStorage<T> {
    public DataStorage(int expire) {
        this.expire = expire;
    }

    private final ConcurrentSkipListMap<Long, List<T>> storageMap = new ConcurrentSkipListMap<>();

    private final List<String> keyNames = new CopyOnWriteArrayList<>();

    private final int expire ; // 数据过期时间，单位为 s

    public void store(Map<String, T> dataMap, long timestamp) {
        List<T> dataList = toMetricsArray(dataMap);
        List<T> unmodifiableList = Collections.unmodifiableList(dataList); //作为不可变数组存储
        storageMap.put(timestamp, unmodifiableList);
        storageMap.headMap(timestamp - expire * 1000L).clear(); //清除过期数据
    }

    public List<DataItem<T>> get(String key) {
        int index = indexOf(key);
        var snapshot = new ArrayList<>(storageMap.entrySet()); // 创建快照
        return snapshot.stream()
                .filter(entry -> index < entry.getValue().size())
                .map(entry -> new DataItem<>(entry.getKey(), key, entry.getValue().get(index)))
                .toList();
    }

    public List<DataItem<T>> get(String key, int limit){
        int index = indexOf(key);
        var snapshot = new ArrayList<>(storageMap.entrySet()); // 创建快照
        return snapshot.stream()
                .skip(snapshot.size() - limit)
                .filter(entry -> index < entry.getValue().size())
                .map(entry -> new DataItem<>(entry.getKey(), key, entry.getValue().get(index)))
                .toList();
    }

    public List<String> getKeyNames() {
        return Collections.unmodifiableList(this.keyNames);
    }

    public List<DataItem<T>> get(List<String> keys) {
        var snapshot = new ArrayList<>(storageMap.entrySet()); // 创建快照
        return snapshot.stream()
                .flatMap(entry -> keys.stream().map(key -> new DataItem<>(entry.getKey(), key, entry.getValue().get(indexOf(key)))))
                .toList();
    }

    public List<DataItem<T>> get(List<String> keys, int limit) {
        var snapshot = new ArrayList<>(storageMap.entrySet()); // 创建快照
        return snapshot.stream()
                .skip( snapshot.size() < limit? 0 : snapshot.size() - limit)
                .flatMap(entry -> keys.stream().map(key -> new DataItem<>(entry.getKey(), key, entry.getValue().get(indexOf(key)))))
                .toList();
    }

    public List<DataItem<T>> getAll(){
        return get(this.keyNames);
    }

    public List<DataItem<T>> getAll(int limit){
        return get(this.keyNames,limit);
    }

    public Map<Long,List<T>> getStoreMap(){
        return Collections.unmodifiableMap(this.storageMap);
    }



    /**
     * 根据 {@code storageMap}获取 指标的各子项的具体数据的列表 <br/>
     * {@code storageMap}是 子项名称-子项值 的键值对 <br/>
     * 子项名称会按顺序记录到{@link #keyNames} <br/>
     * 当记录过程中{@code storageMap} 有新的子项，该子项名称会添加到{@link #keyNames}列表的最后
     * 当记录过程中{@code storageMap} 有子项的缺失，{@link #keyNames}列表不变，缺失的子项值在返回的列表中值为{@code null}
     *
     * @param dataMap 指标的子项名称-子项值 的键值对
     * @return 返回符合 {@link #keyNames} 顺序的子项值列表
     */
    private List<T> toMetricsArray(Map<String, T> dataMap) {
        //遍历tempInfoMap的传感器名称，记录新传感器
        synchronized (this) {
            dataMap.keySet().stream().sorted().forEach(key -> {
                if (!this.keyNames.contains(key))
                    this.keyNames.add(key);
            });
        }
        //遍历this.sensorNames,按照顺序根据tempInfoMap信息获取温度列表
        return this.keyNames.stream()
                .map(dataMap::get).toList();
    }


    private int indexOf(String key) {
        int index = keyNames.indexOf(key);
        if (index == -1)
            throw new IllegalArgumentException("当前存储没有键:" + key);
        return index;
    }


}
