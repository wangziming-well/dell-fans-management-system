package com.wzm.fans.api;

import lombok.Data;

@Data
public class SessionsRequest {
    private String userName;
    private String password;

}