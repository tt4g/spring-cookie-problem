// This file was written from the original code which has the following license:
//
// https://github.com/spring-projects/spring-security/blob/5.3.4.RELEASE/web/src/main/java/org/springframework/security/web/server/csrf/WebSessionServerCsrfTokenRepository.java
//
// Copyright 2002-2017 the original author or authors.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       https://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.github.tt4g.spring.cookie.problem;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class WebSessionServerTraceIdRepository implements TraceIdRepository {

    private final String sessionAttributeName;

    public WebSessionServerTraceIdRepository() {

        this.sessionAttributeName =
            WebSessionServerTraceIdRepository.class.getName();
    }

    @Override
    public Mono<TraceId> generate(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> new TraceId(UUID.randomUUID().toString()))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<TraceId> load(ServerWebExchange exchange) {
        return exchange.getSession()
            .filter(webSession ->
                webSession.getAttributes().containsKey(this.sessionAttributeName))
            .map(webSession ->
                webSession.getAttribute(this.sessionAttributeName));
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, TraceId traceId) {
        return exchange.getSession()
            .doOnNext(webSession -> {
                Map<String, Object> attributes = webSession.getAttributes();

                if (traceId == null) {
                    attributes.remove(this.sessionAttributeName);
                } else {
                    attributes.put(this.sessionAttributeName, traceId);
                }
            })
            .then();
    }

}

