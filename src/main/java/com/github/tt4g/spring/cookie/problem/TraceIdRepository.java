package com.github.tt4g.spring.cookie.problem;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface TraceIdRepository {

    /**
     * Generate {@link TraceId}.<br>
     *
     * @param exchange {@link ServerWebExchange}
     * @return {@link TraceId}
     */
    Mono<TraceId> generate(ServerWebExchange exchange);

    /**
     * Load {@link TraceId}.<br>
     *
     * @param exchange {@link ServerWebExchange}
     * @return {@link TraceId}
     */
    Mono<TraceId> load(ServerWebExchange exchange);

    /**
     * Save {@link TraceId}.<br>
     *
     * @param exchange {@link ServerWebExchange}
     * @param traceId {@link TraceId}. Remove {@link TraceId} if specified <code>null</code>.
     * @return
     */
    Mono<Void> save(ServerWebExchange exchange, TraceId traceId);

}

