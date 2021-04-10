package com.github.tt4g.spring.cookie.problem;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Generate CSRF-token in Single Page Application.<br>
 *
 * See: https://github.com/spring-projects/spring-security/issues/6046
 */
public class GenerateCsrfTokenWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        exchange.getResponse().beforeCommit(() ->
            Mono.defer(() -> this.subscribeCsrfToken(exchange)));

        return chain.filter(exchange);
    }

    private Mono<Void> subscribeCsrfToken(ServerWebExchange exchange) {
        Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            return csrfToken.then();
        } else {
            return Mono.empty();
        }
    }

}

