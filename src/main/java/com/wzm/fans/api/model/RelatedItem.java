package com.wzm.fans.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
        public class RelatedItem {
            @JsonProperty("@odata.id")
            private String odataId;
        }