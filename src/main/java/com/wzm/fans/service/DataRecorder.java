package com.wzm.fans.service;


import com.wzm.fans.pojo.DataItem;
import com.wzm.fans.util.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.yaml.snakeyaml.util.EnumUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 通过线程池定时任务定时记录数据
 * 可以分为多级存储 每一级可以自定义记录间隔和过期时间
 * 例如：第一级：每秒记录一次，3600s后数据过期
 * 第二级：每分钟记录一次，12小时后数据过期
 * 注意每级的记录间隔必须是上一级的整数倍(以秒为单位的数值),并且每级的记录间隔必须小于上一集的数据过期时间
 */
public class DataRecorder {
    public List<String> getKeys(String category) {
        List<DataStorage<Double>> dataStorageList = this.categoryTierDataStorage.getDataStorageList(category);
        return dataStorageList.get(0).getKeyNames();
    }

    @Data
    @AllArgsConstructor
    public static class StorageTierStrategy {

        private final int interval; // 数据记录间隔 ，单位秒

        private final int expire;  // 数据过期时间 ，单位秒
    }

    public static class DataContainer {

        private final Map<String,Map<String,Double>> dataMap = new HashMap<>();

        public void add(String category,Map<String,Double> data){
            dataMap.put(category,data);
        }
    }

    @Data
    private static class CategoryTierDataStorage{
        private final Map<String, List<DataStorage<Double>>> categoryDataStorageListMap = new HashMap<>();
        private final  Supplier<List<DataStorage<Double>>>  dataStorageListInitSupplier;

        /**
         * 记录层级间尚未计算的间隔次数，
         * 例如第一层间隔每秒一次，第二层间隔每5秒一次
         * 当定时任务在3秒内运行了3次后，  tierIntervalTimeRecords[1] 就为3
         * 当定时任务在3秒内运行了5次后， 当前类就根据第一层的前5个数据计算出第二层的第一个数据，并将  tierIntervalTimeRecords[1] 置为0
         */
        private final Map<String,Integer[]> tierIntervalTimeRecordsMap = new HashMap<>();

        private final int timeRecordListLength;

        private CategoryTierDataStorage( int timeRecordListLength,Supplier<List<DataStorage<Double>>>  dataStorageListInitSupplier) {
            this.dataStorageListInitSupplier = dataStorageListInitSupplier;
            this.timeRecordListLength = timeRecordListLength;
        }

        public  List<DataStorage<Double>> getOrCreateDataStorageList(String category){
            return this.categoryDataStorageListMap.computeIfAbsent(category,key -> this.dataStorageListInitSupplier.get());
        }

        public  Integer[] getOrCreateTierIntervalTimeRecords(String category){
            return this.tierIntervalTimeRecordsMap.computeIfAbsent(category,key -> {
                Integer[] result = new Integer[timeRecordListLength];
                Arrays.fill(result, 0);
                return result;
            });
        }

        public List<DataStorage<Double>> getDataStorageList(String category){
            List<DataStorage<Double>> dataStorages = this.categoryDataStorageListMap.get(category);
            if (dataStorages == null)
                throw new IllegalArgumentException("不存在的category:" + category);
            return dataStorages;


        }
    }

    private final CategoryTierDataStorage categoryTierDataStorage ;

    private final ScheduledExecutorService executorService;

    private final Supplier<DataContainer> dataSupplier;


    private final int basePeriod; // 定时任务的基础间隔
    /**
     * 层级间的数据记录间隔倍率，
     * 例如 第一层间隔是每秒一次，第二层间隔是每5秒一次，第三层间隔是每60秒一次，
     * 那么tierIntervalRate[1]= 5，tierIntervalRate[2] = 12
     * 其中tierIntervalRate[0] 固定为0，表示定时任务执行一次就记录一次。
     */
    private final int[] tierIntervalRates;
    /**
     * 记录层级间尚未计算的间隔次数，
     * 例如第一层间隔每秒一次，第二层间隔每5秒一次
     * 当定时任务在3秒内运行了3次后，  tierIntervalTimeRecords[1] 就为3
     * 当定时任务在3秒内运行了5次后， 当前类就根据第一层的前5个数据计算出第二层的第一个数据，并将  tierIntervalTimeRecords[1] 置为0
     */

    public DataRecorder(ScheduledExecutorService executorService, Supplier<DataContainer> dataSupplier, List<StorageTierStrategy> storageStrategy) {
        checkStrategy(storageStrategy);

        this.executorService = executorService;
        this.dataSupplier = dataSupplier;
        this.tierIntervalRates = calculateTierIntervalRate(storageStrategy);
        this.categoryTierDataStorage = new CategoryTierDataStorage(tierIntervalRates.length,
                () -> storageStrategy
                .stream().map(storageTierStrategy -> new DataStorage<Double>(storageTierStrategy.expire)).toList());
        this.basePeriod = storageStrategy.get(0).interval;
    }

    private int[] calculateTierIntervalRate(List<StorageTierStrategy> storageStrategy) {
        int tier = storageStrategy.size();
        int[] result = new int[tier];

        int previousInterval = storageStrategy.getFirst().interval;
        for (int i = 1; i < tier; i++) {
            int currentInterval = storageStrategy.get(i).interval;
            result[i] = currentInterval / previousInterval;
            previousInterval = currentInterval;
        }
        return result;
    }

    private void checkStrategy(List<StorageTierStrategy> storageStrategy) {
        if (storageStrategy.isEmpty())
            throw new IllegalArgumentException("存储层策略为空");
        StorageTierStrategy prevStrategy = storageStrategy.getFirst();
        for (int i = 1; i < storageStrategy.size(); i++) {
            StorageTierStrategy currStrategy = storageStrategy.get(i);
            if (currStrategy.interval > prevStrategy.expire)
                throw new IllegalArgumentException("每级的记录间隔必须小于上一级的数据过期时间");
            if (currStrategy.interval % prevStrategy.interval != 0)
                throw new IllegalArgumentException("每级的记录间隔必须是上一级的整数倍(以秒为单位的数值)");
            prevStrategy = currStrategy;
        }
    }

    public List<DataItem<Double>> getData(String category,int tier){
        List<DataStorage<Double>> dataStorageList = this.categoryTierDataStorage.getDataStorageList(category);
        return dataStorageList.get(tier - 1).getAll();
    }

    public Map<Long,List<Double>> getStoreMap(String category,int tier){
        List<DataStorage<Double>> dataStorageList = this.categoryTierDataStorage.getDataStorageList(category);
        return dataStorageList.get(tier -1).getStoreMap();
    }

    public List<DataItem<Double>> getLatest(String category){
        List<DataStorage<Double>> dataStorageList = this.categoryTierDataStorage.getDataStorageList(category);
        return  dataStorageList.get(0).getAll(1);
    }

    public void start() {
        executorService.scheduleAtFixedRate(this::mainTask, 0, basePeriod, TimeUnit.SECONDS);
    }

    private void mainTask() {
        try {
            long timestamp = System.currentTimeMillis();
            DataContainer dataContainer = dataSupplier.get();
            Map<String, Map<String, Double>> categoryDataMap = dataContainer.dataMap;
            for (Map.Entry<String,Map<String,Double>> entry : categoryDataMap.entrySet()){
                String category = entry.getKey();
                Map<String, Double> dataMap = entry.getValue();
                List<DataStorage<Double>> dataStorageList = categoryTierDataStorage.getOrCreateDataStorageList(category);
                Integer[] tierIntervalTimeRecords = categoryTierDataStorage.getOrCreateTierIntervalTimeRecords(category);
                doStore(dataStorageList,dataMap,timestamp,tierIntervalTimeRecords);
            }
        } catch (Exception e) {
            System.err.println("Error in task: " + e.getMessage());
            e.printStackTrace(); // 显示异常堆栈信息
        }
    }
    public void doStore(List<DataStorage<Double>> dataStorageList,Map<String, Double> dataMap
            ,long timestamp,Integer[] tierIntervalTimeRecords){
        for (int i = 0; i < dataStorageList.size(); i++) {
            int tierIntervalTimeRecord = tierIntervalTimeRecords[i]; // 上一次未计算的次数次数
            int tierIntervalRate = tierIntervalRates[i];  // 与上一层记录间隔的比例

            if (tierIntervalRate <= tierIntervalTimeRecord) { //说明该层需要根据上一层的前几个数据计算出本次的本层
                DataStorage<Double> currDataStorage = dataStorageList.get(i);
                if (i == 0) { //对于第1层存储，直接记录数据
                    currDataStorage.store(dataMap, timestamp);
                } else { // 对于第二层即以上的存储，根据上一层的数据计算得出要存储的数据
                    DataStorage<Double> previousDataStorage = dataStorageList.get(i - 1);
                    Map<String, Double> currDataMap = calculatePreviousData(previousDataStorage, tierIntervalRate);
                    currDataStorage.store(currDataMap, timestamp);
                }

                if (i + 1 < tierIntervalTimeRecords.length)
                    tierIntervalTimeRecords[i + 1]++; //将后一层记录增加1;
                tierIntervalTimeRecords[i] = 0; //将当前层记录重置为0;
            }
        }
    }


    //计算一段时间的平均值
    private Map<String, Double> calculatePreviousData(DataStorage<Double> previousDataStorage, int limit) {
        List<DataItem<Double>> previousDataListItem = previousDataStorage.getAll(limit);
        ConcurrentHashMap<String, Double> resultMap = new ConcurrentHashMap<>();
        previousDataListItem.forEach(dataItem -> resultMap.merge(dataItem.getName(), dataItem.getValue(), Double::sum));
        for (String key : resultMap.keySet()) {
            resultMap.compute(key, (k, value) -> value == null? 0: Math.floor(value /  limit * 100) / 100 );
        }

        return resultMap;
    }
}
