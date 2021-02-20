package com.github.tt4g.spring.cookie.problem;

import java.util.Optional;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Add {@link TraceId} to {@link Context}.<br>
 *
 * If {@link TraceId} specified by request, use it.
 */
public class TraceIdWebFilter implements WebFilter {

    private final String headerName;

    private final TraceIdRepository traceIdRepository;

    public TraceIdWebFilter(TraceIdRepository traceIdRepository) {

        this.headerName = "X-Trace-Id";
        this.traceIdRepository = traceIdRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return this.load(exchange)
            // Handle Mono<TraceId>.empty()
            .switchIfEmpty(Mono.defer(() -> chain.filter(exchange).then(Mono.empty())))
            .flatMap(traceId -> {
                // Output response
                exchange.getResponse().getHeaders().add(this.headerName, traceId.getTraceId());

                return chain.filter(exchange)
                    .contextWrite(context ->
                        // Reactor Context に TraceID を追加する。
                        context.put(ReactorMdcTracer.CONTEXT_KEY, traceId));
            });
    }

    private Mono<TraceId> load(ServerWebExchange exchange) {
        // Get TraceId from request HTTP header.
        return extractFromHeader(exchange)
            .orElseGet(() ->
                // Can not get TraceId from request then load TraceId with TraceIdRepository.
                this.traceIdRepository.load(exchange)
                    // Generate new TraceId when can not get TraceId.
                    .switchIfEmpty(Mono.defer(() -> generate(exchange))));
    }

    private Optional<Mono<TraceId>> extractFromHeader(ServerWebExchange exchange) {
        Optional<String> headerTraceIdOptional =
            Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(this.headerName));

        return headerTraceIdOptional
            .filter(headerTraceId -> headerTraceId != null && !headerTraceId.isEmpty())
            .map(nonEmptyTraceId ->
                Mono.just(new TraceId(nonEmptyTraceId))
                    .flatMap(traceId ->
                        this.traceIdRepository.save(exchange, traceId)
                            .thenReturn(traceId)));
    }

    private Mono<TraceId> generate(ServerWebExchange exchange) {
        return this.traceIdRepository.generate(exchange)
            .flatMap(traceId ->
                // Save generated TraceId.
                this.traceIdRepository.save(exchange, traceId)
                    .thenReturn(traceId));
    }

}

