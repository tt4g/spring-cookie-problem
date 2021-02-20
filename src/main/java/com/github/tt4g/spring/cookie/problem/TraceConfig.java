package com.github.tt4g.spring.cookie.problem;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TraceConfig {

    @PostConstruct
    public void installReactorHook() {
        ReactorMdcTracer.installHook();
    }

    @PreDestroy
    public void resetReactorHook() {
        ReactorMdcTracer.resetHook();
    }

    @Bean
    public TraceIdWebFilter traceIdWebFilter() {
        WebSessionServerTraceIdRepository webSessionServerTraceIdRepository =
            new WebSessionServerTraceIdRepository();

        return new TraceIdWebFilter(webSessionServerTraceIdRepository);
    }

}

