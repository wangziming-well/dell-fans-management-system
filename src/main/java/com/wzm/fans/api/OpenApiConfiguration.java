package com.wzm.fans.api;

import com.wzm.fans.properties.IdracProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class OpenApiConfiguration {

    @Autowired
    IdracProperties idracProperties;


    @Bean
    @Scope("prototype")
    public WebClient.Builder webClientBuilder() {
        WebClient.Builder builder = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(60))));

        return builder;
    }

    private <T> T createClient(WebClient webClient, Class<T> clazz) {
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clazz);
    }

    private <T> T createOpenApiClient(WebClient.Builder webClientBuilder, String baseUrl, MultiValueMap<String,String> headers, Class<T> apiClass) {
        if (headers != null)
            webClientBuilder.defaultHeaders(defaultHeaders -> defaultHeaders.addAll(headers));
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
        return createClient(webClient, apiClass);
    }

    @Bean
    public RedfishApi redfishApi(WebClient.Builder webClientBuilder) {
        String baseUrl = String.format("http://%s/redfish/v1",idracProperties.getHost()) ;
        return createOpenApiClient(webClientBuilder,baseUrl,null, RedfishApi.class);
    }
}
