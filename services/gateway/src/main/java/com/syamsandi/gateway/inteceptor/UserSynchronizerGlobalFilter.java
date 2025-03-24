package com.syamsandi.gateway.inteceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizerGlobalFilter implements GlobalFilter, Ordered {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JwtDecoder jwtDecoder;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_PREFIX = "synced:user:";
    private static final Duration TTL = Duration.ofSeconds(10);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String tokenValue = authHeader.substring(7);
            try {
                Jwt jwt = jwtDecoder.decode(tokenValue);
                String userId = jwt.getSubject();

                String cacheKey = CACHE_PREFIX + userId;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                    log.debug("User {} already synced, skipping", userId);
                } else {

                    kafkaTemplate.send("user-synchronize", authHeader);
                    redisTemplate.opsForValue().set(cacheKey, "true", TTL);
                    log.debug("User {} synchronization triggered and cached", userId);
                }
            } catch (JwtException e) {
                log.warn("Invalid JWT token, skipping sync: {}", tokenValue);
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}