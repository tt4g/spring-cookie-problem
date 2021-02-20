package com.github.tt4g.spring.cookie.problem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class WebConfig {

    @Bean
    public GenerateCsrfTokenWebFilter generateCsrfTokenWebFilter() {
        return new GenerateCsrfTokenWebFilter();
    }

}
