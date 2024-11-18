package com.wzm.fans.api;

import com.wzm.fans.util.ReflectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Component
class RedfishBaseUrlChanger {

    private final WebClient redfishWebClient;

    private static final String FIELD_NAME = "uriBuilderFactory";

    RedfishBaseUrlChanger(WebClient redfishWebClient) {
        this.redfishWebClient = redfishWebClient;
    }

    public void changeBaseUrl(String baseUrl){
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory(baseUrl);
        ReflectUtils.setField(this.redfishWebClient,redfishWebClient.getClass(),
                FIELD_NAME,defaultUriBuilderFactory);
    }
}
