package com.wzm.fans.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PowerResponse {

    @JsonProperty("@odata.context")
    private String odataContext;

    @JsonProperty("@odata.id")
    private String odataId;

    @JsonProperty("@odata.type")
    private String odataType;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("PowerControl")
    private List<PowerControl> powerControl;

    @JsonProperty("PowerControl@odata.count")
    private int powerControlCount;

    @JsonProperty("PowerSupplies")
    private List<PowerSupply> powerSupplies;

    @JsonProperty("PowerSupplies@odata.count")
    private int powerSuppliesCount;

    @JsonProperty("Redundancy")
    private List<Object> redundancy;

    @JsonProperty("Redundancy@odata.count")
    private int redundancyCount;

    @JsonProperty("Voltages")
    private List<Voltage> voltages;

    @JsonProperty("Voltages@odata.count")
    private int voltagesCount;

    @Data
    public static class PowerControl {

        @JsonProperty("@odata.context")
        private String odataContext;

        @JsonProperty("@odata.id")
        private String odataId;

        @JsonProperty("@odata.type")
        private String odataType;

        @JsonProperty("MemberId")
        private String memberId;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("PowerAllocatedWatts")
        private int powerAllocatedWatts;

        @JsonProperty("PowerAvailableWatts")
        private int powerAvailableWatts;

        @JsonProperty("PowerCapacityWatts")
        private int powerCapacityWatts;

        @JsonProperty("PowerConsumedWatts")
        private double powerConsumedWatts;

        @JsonProperty("PowerLimit")
        private PowerLimit powerLimit;

        @JsonProperty("PowerMetrics")
        private PowerMetrics powerMetrics;

        @JsonProperty("PowerRequestedWatts")
        private int powerRequestedWatts;

        @JsonProperty("RelatedItem")
        private List<RelatedItem> relatedItem;

        @JsonProperty("RelatedItem@odata.count")
        private int relatedItemCount;

        @Data
        public static class PowerLimit {
            @JsonProperty("CorrectionInMs")
            private int correctionInMs;

            @JsonProperty("LimitException")
            private String limitException;

            @JsonProperty("LimitInWatts")
            private int limitInWatts;
        }

        @Data
        public static class PowerMetrics {
            @JsonProperty("AverageConsumedWatts")
            private int averageConsumedWatts;

            @JsonProperty("IntervalInMin")
            private int intervalInMin;

            @JsonProperty("MaxConsumedWatts")
            private int maxConsumedWatts;

            @JsonProperty("MinConsumedWatts")
            private int minConsumedWatts;
        }


    }

    @Data
    public static class PowerSupply {

        @JsonProperty("@odata.context")
        private String odataContext;

        @JsonProperty("@odata.id")
        private String odataId;

        @JsonProperty("@odata.type")
        private String odataType;

        @JsonProperty("Assembly")
        private RelatedItem assembly;

        @JsonProperty("EfficiencyPercent")
        private double efficiencyPercent;

        @JsonProperty("FirmwareVersion")
        private String firmwareVersion;

        @JsonProperty("HotPluggable")
        private boolean hotPluggable;

        @JsonProperty("InputRanges")
        private List<InputRange> inputRanges;

        @JsonProperty("InputRanges@odata.count")
        private int inputRangesCount;

        @JsonProperty("LineInputVoltageType")
        private String lineInputVoltageType;

        @JsonProperty("Manufacturer")
        private String manufacturer;

        @JsonProperty("MemberId")
        private String memberId;

        @JsonProperty("Model")
        private String model;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Oem")
        private Oem oem;

        @JsonProperty("PartNumber")
        private String partNumber;

        @JsonProperty("PowerCapacityWatts")
        private int powerCapacityWatts;

        @JsonProperty("PowerInputWatts")
        private int powerInputWatts;

        @JsonProperty("PowerOutputWatts")
        private int powerOutputWatts;

        @JsonProperty("PowerSupplyType")
        private String powerSupplyType;

        @JsonProperty("Redundancy")
        private List<Object> redundancy;

        @JsonProperty("Redundancy@odata.count")
        private int redundancyCount;

        @JsonProperty("RelatedItem")
        private List<RelatedItem> relatedItem;

        @JsonProperty("RelatedItem@odata.count")
        private int relatedItemCount;

        @JsonProperty("SerialNumber")
        private String serialNumber;

        @JsonProperty("SparePartNumber")
        private String sparePartNumber;

        @JsonProperty("Status")
        private Status status;

        @Data
        public static class InputRange {
            @JsonProperty("InputType")
            private String inputType;

            @JsonProperty("MaximumFrequencyHz")
            private int maximumFrequencyHz;

            @JsonProperty("MaximumVoltage")
            private int maximumVoltage;

            @JsonProperty("MinimumFrequencyHz")
            private int minimumFrequencyHz;

            @JsonProperty("MinimumVoltage")
            private int minimumVoltage;

            @JsonProperty("OutputWattage")
            private int outputWattage;
        }

        @Data
        public static class Oem {
            @JsonProperty("Dell")
            private Dell dell;

            @Data
            public static class Dell {
                @JsonProperty("DellPowerSupply")
                private DellPowerSupply dellPowerSupply;

                @Data
                public static class DellPowerSupply {
                    @JsonProperty("@odata.context")
                    private String odataContext;

                    @JsonProperty("@odata.id")
                    private String odataId;

                    @JsonProperty("@odata.type")
                    private String odataType;

                    @JsonProperty("IsSwitchingSupply")
                    private boolean isSwitchingSupply;

                    @JsonProperty("Links")
                    private Links links;

                    @Data
                    public static class Links {
                        @JsonProperty("DellPSNumericSensorCollection")
                        private List<RelatedItem> dellPSNumericSensorCollection;
                    }
                }
            }
        }
    }

    @Data
    public static class Voltage {

        @JsonProperty("@odata.context")
        private String odataContext;

        @JsonProperty("@odata.id")
        private String odataId;

        @JsonProperty("@odata.type")
        private String odataType;

        @JsonProperty("MemberId")
        private String memberId;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("PhysicalContext")
        private String physicalContext;

        @JsonProperty("ReadingVolts")
        private int readingVolts;

        @JsonProperty("RelatedItem")
        private List<RelatedItem> relatedItem;

        @JsonProperty("RelatedItem@odata.count")
        private int relatedItemCount;

        @JsonProperty("SensorNumber")
        private int sensorNumber;

        @JsonProperty("Status")
        private Status status;
    }

    @Data
    public static class Status {
        @JsonProperty("Health")
        private String health;

        @JsonProperty("State")
        private String state;
    }
}
