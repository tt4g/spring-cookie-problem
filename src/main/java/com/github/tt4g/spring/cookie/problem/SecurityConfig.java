package com.github.tt4g.spring.cookie.problem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfWebFilter;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity http) {

        WebSessionServerSecurityContextRepository webSessionServerSecurityContextRepository =
            new WebSessionServerSecurityContextRepository();

        return http
            .securityMatcher(ServerWebExchangeMatchers.anyExchange())
            .authorizeExchange(authorizeExchangeSpec -> {
                authorizeExchangeSpec
                    .anyExchange()
                    .permitAll();
            })
            .anonymous().disable()
            .securityContextRepository(webSessionServerSecurityContextRepository)
            .csrf(csrfSpec -> {
                // Cookie name of XSRF token is "XSRF-TOKEN" and
                // get token from HTTP header "X-XSRF-TOKEN".
                CookieServerCsrfTokenRepository cookieServerCsrfTokenRepository =
                    CookieServerCsrfTokenRepository.withHttpOnlyFalse();
                cookieServerCsrfTokenRepository.setCookieName("XSRF-TOKEN");
                cookieServerCsrfTokenRepository.setHeaderName("X-XSRF-TOKEN");
                csrfSpec.csrfTokenRepository(cookieServerCsrfTokenRepository);

                csrfSpec.accessDeniedHandler(
                    new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN));
                csrfSpec.tokenFromMultipartDataEnabled(false);
                csrfSpec.requireCsrfProtectionMatcher(CsrfWebFilter.DEFAULT_CSRF_MATCHER);
            })
            .httpBasic().disable()
            .formLogin().disable()
            .logout(logoutSpec -> {
                logoutSpec.requiresLogout(
                    ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/logout"));

                logoutSpec.logoutSuccessHandler(
                    new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.OK));
            })
            .exceptionHandling(exceptionHandlingSpec -> {
                exceptionHandlingSpec.accessDeniedHandler(
                    new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN));
            })
            .headers(headerSpec -> {
                headerSpec.cache().disable();
                headerSpec.contentTypeOptions();
                headerSpec.hsts().disable();
                headerSpec.frameOptions(frameOptionsSpec -> {
                    frameOptionsSpec.mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY);
                });
                headerSpec.xssProtection();
                headerSpec.referrerPolicy(referrerPolicySpec -> {
                    referrerPolicySpec.policy(ReferrerPolicy.SAME_ORIGIN);
                });
            })
            .cors().disable()
            .requestCache().disable()
            .build();
    }

}
