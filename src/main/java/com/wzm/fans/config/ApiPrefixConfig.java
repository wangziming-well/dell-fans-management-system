package com.wzm.fans.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Configuration
public class ApiPrefixConfig implements WebMvcRegistrations {


    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiHandlerMapping("/api");
    }

    static class ApiHandlerMapping extends RequestMappingHandlerMapping {
        private final String prefix;

        public ApiHandlerMapping(String prefix) {
            this.prefix = prefix;
        }

        @Override
        protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
            RequestMappingInfo prefixedMapping = RequestMappingInfo
                    .paths(prefix)
                    .build()
                    .combine(mapping);
            super.registerHandlerMethod(handler, method, prefixedMapping);
        }


    }
}