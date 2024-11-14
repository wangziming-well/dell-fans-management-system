package com.wzm.fans.api;

import lombok.Data;

@Data
public class SessionsResponse {
    private String id;
    private String name;
    private String userName;
    private boolean active;
    private int timeout;
    private Links links;

    @Data
    public static class Links {
        private String session;
    }
}