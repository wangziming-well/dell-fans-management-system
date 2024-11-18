package com.wzm.fans.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ThermalResponse {
    @JsonProperty("@odata.context")
    private String odataContext;

    @JsonProperty("@odata.id")
    private String odataId;

    @JsonProperty("@odata.type")
    private String odataType;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Fans")
    private List<Fan> fans;

    @JsonProperty("Fans@odata.count")
    private int fansCount;

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Redundancy")
    private List<Redundancy> redundancy;

    @JsonProperty("Redundancy@odata.count")
    private int redundancyCount;

    @JsonProperty("Temperatures")
    private List<Temperature> temperatures;

    @JsonProperty("Temperatures@odata.count")
    private int temperaturesCount;

    @Data
    public static class Fan {
        @JsonProperty("@odata.id")
        private String odataId;

        @JsonProperty("@odata.type")
        private String odataType;

        @JsonProperty("Assembly")
        private Assembly assembly;

        @JsonProperty("FanName")
        private String fanName;

        @JsonProperty("LowerThresholdCritical")
        private Integer lowerThresholdCritical;

        @JsonProperty("LowerThresholdFatal")
        private Integer lowerThresholdFatal;

        @JsonProperty("LowerThresholdNonCritical")
        private Integer lowerThresholdNonCritical;

        @JsonProperty("MaxReadingRange")
        private Integer maxReadingRange;

        @JsonProperty("MemberId")
        private String memberId;

        @JsonProperty("MinReadingRange")
        private Integer minReadingRange;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("PhysicalContext")
        private String physicalContext;

        @JsonProperty("Reading")
        private Integer reading;

        @JsonProperty("ReadingUnits")
        private String readingUnits;

        @JsonProperty("Redundancy")
        private List<Object> redundancy;

        @JsonProperty("Redundancy@odata.count")
        private int redundancyCount;

        @JsonProperty("RelatedItem")
        private List<RelatedItem> relatedItem;

        @JsonProperty("RelatedItem@odata.count")
        private int relatedItemCount;

        @JsonProperty("Status")
        private Status status;

        @JsonProperty("UpperThresholdCritical")
        private Integer upperThresholdCritical;

        @JsonProperty("UpperThresholdFatal")
        private Integer upperThresholdFatal;

        @JsonProperty("UpperThresholdNonCritical")
        private Integer upperThresholdNonCritical;

        @Data
        public static class Assembly {
            @JsonProperty("@odata.id")
            private String odataId;
        }

        @Data
        public static class RelatedItem {
            @JsonProperty("@odata.id")
            private String odataId;
        }

        @Data
        public static class Status {
            @JsonProperty("Health")
            private String health;

            @JsonProperty("State")
            private String state;
        }
    }

    @Data
    public static class Redundancy {
        @JsonProperty("@odata.id")
        private String odataId;

        @JsonProperty("@odata.type")
        private String odataType;

        @JsonProperty("MaxNumSupported")
        private int maxNumSupported;

        @JsonProperty("MemberId")
        private String memberId;

        @JsonProperty("MinNumNeeded")
        private int minNumNeeded;

        @JsonProperty("Mode")
        private String mode;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("RedundancyEnabled")
        private boolean redundancyEnabled;

        @JsonProperty("RedundancySet")
        private List<Object> redundancySet;

        @JsonProperty("RedundancySet@odata.count")
        private int redundancySetCount;

        @JsonProperty("Status")
        private Status status;

        @Data
        public static class Status {
            @JsonProperty("Health")
            private String health;

            @JsonProperty("State")
            private String state;
        }
    }

    @Data
    public static class Temperature {
        @JsonProperty("@odata.context")
        private String odataContext;

        @JsonProperty("@odata.id")
        private String odataId;

        @JsonProperty("@odata.type")
        private String odataType;

        @JsonProperty("LowerThresholdCritical")
        private Integer lowerThresholdCritical;

        @JsonProperty("LowerThresholdFatal")
        private Integer lowerThresholdFatal;

        @JsonProperty("LowerThresholdNonCritical")
        private Integer lowerThresholdNonCritical;

        @JsonProperty("MaxReadingRangeTemp")
        private Integer maxReadingRangeTemp;

        @JsonProperty("MemberId")
        private String memberId;

        @JsonProperty("MinReadingRangeTemp")
        private Integer minReadingRangeTemp;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("PhysicalContext")
        private String physicalContext;

        @JsonProperty("ReadingCelsius")
        private Integer readingCelsius;

        @JsonProperty("RelatedItem")
        private List<RelatedItem> relatedItem;

        @JsonProperty("RelatedItem@odata.count")
        private int relatedItemCount;

        @JsonProperty("SensorNumber")
        private int sensorNumber;

        @JsonProperty("Status")
        private Status status;

        @JsonProperty("UpperThresholdCritical")
        private Integer upperThresholdCritical;

        @JsonProperty("UpperThresholdFatal")
        private Integer upperThresholdFatal;

        @JsonProperty("UpperThresholdNonCritical")
        private Integer upperThresholdNonCritical;

        @Data
        public static class RelatedItem {
            @JsonProperty("@odata.id")
            private String odataId;
        }

        @Data
        public static class Status {
            @JsonProperty("Health")
            private String health;

            @JsonProperty("State")
            private String state;
        }
    }
}