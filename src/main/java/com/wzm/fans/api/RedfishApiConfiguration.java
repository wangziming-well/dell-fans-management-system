package com.wzm.fans.api;

import com.wzm.fans.util.ConfigUtils;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
public class RedfishApiConfiguration {

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
                }).responseTimeout(Duration.ofSeconds(10));

        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    @Bean
    public WebClient redfishWebClient(){
        WebClient.Builder builder = webClientBuilder();
        String host = ConfigUtils.get("idrac.host");
        String username = ConfigUtils.get("idrac.username");
        String password = ConfigUtils.get("idrac.password");

        builder.defaultHeaders(defaultHeaders -> defaultHeaders.setBasicAuth(username,password));
        String baseUrl = RedfishApi.baseUrl(host);
        return builder.
                baseUrl(baseUrl).build();
    }

    @Bean
    public RedfishApi redfishApi(WebClient redfishWebClient) {
        WebClientAdapter adapter = WebClientAdapter.create(redfishWebClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(RedfishApi.class);
    }
}
