package com.wzm.fans.service;

import com.wzm.fans.api.RedfishApi;
import com.wzm.fans.api.SessionsRequest;
import com.wzm.fans.api.SessionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedfishService {
    private final RedfishApi api;

    public RedfishService(RedfishApi api) {
        this.api = api;
    }

    public void login(){
        SessionsRequest sessionsRequest = new SessionsRequest();
        sessionsRequest.setUserName("root");
        sessionsRequest.setPassword("wang998321");

        SessionsResponse sessions = api.sessions(sessionsRequest);
        System.out.println(sessions);

    }

}
