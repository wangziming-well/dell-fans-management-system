package com.wzm.fans.api;

import com.wzm.fans.properties.IdracProperties;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
public class RedfishApiConfiguration {

    @Autowired
    IdracProperties idracProperties;


    public WebClient.Builder webClientBuilder() {
        //忽略SSL证书验证
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> {
                    try {
                        sslContextSpec.sslContext(SslContextBuilder.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE) // 信任所有证书
                                .build());
                    } catch (SSLException e) {
                        throw new RuntimeException("Failed to configure SSL", e);
                    }
                }).responseTimeout(Duration.ofSeconds(60));

        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    private <T> T createClient(WebClient webClient, Class<T> clazz) {
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clazz);
    }

    private <T> T createOpenApiClient(WebClient.Builder webClientBuilder, String baseUrl, Class<T> apiClass) {
        String username = idracProperties.getUsername();
        String password = idracProperties.getPassword();
        webClientBuilder.defaultHeaders(defaultHeaders -> defaultHeaders.setBasicAuth(username,password));
        WebClient webClient = webClientBuilder.
                baseUrl(baseUrl).build();
        return createClient(webClient, apiClass);
    }

    @Bean
    public RedfishApi redfishApi() {
        String host = idracProperties.getHost();
        String baseUrl = String.format("https://%s/redfish/v1",host) ;
        return createOpenApiClient(webClientBuilder(),baseUrl, RedfishApi.class);
    }
}
