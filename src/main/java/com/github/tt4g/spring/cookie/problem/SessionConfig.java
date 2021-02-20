package com.github.tt4g.spring.cookie.problem;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;

/**
 * Session configutation.
 *
 * @author 872887
 */
@EnableSpringWebSession
@Configuration(proxyBeanMethods = false)
public class SessionConfig {

    @Bean
    public ReactiveSessionRepository<MapSession> reactiveSessionRepository(
        SessionProperties sessionProperties) {
        // FIXME: Set session timeout on WebFlux application.
        //  See:
        //  * https://stackoverflow.com/questions/62133366/spring-security-session-timeout-for-spring-reactive
        //  * https://github.com/spring-projects/spring-boot/issues/15757
        //  * https://github.com/spring-projects/spring-boot/issues/23151

        ReactiveMapSessionRepository reactiveMapSessionRepository =
            new ReactiveMapSessionRepository(new ConcurrentHashMap<>());

        int timeout = Math.toIntExact(sessionProperties.getTimeout().toSeconds());
        reactiveMapSessionRepository.setDefaultMaxInactiveInterval(timeout);

        return reactiveMapSessionRepository;
    }

}
