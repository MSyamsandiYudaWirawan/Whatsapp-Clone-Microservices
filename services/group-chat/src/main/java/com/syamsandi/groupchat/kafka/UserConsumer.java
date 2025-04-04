package com.syamsandi.groupchat.kafka;

import com.syamsandi.groupchat.user.UserSynchronizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {
    private final UserSynchronizer userSynchronizer;
    private final JwtDecoder jwtDecoder;


    @KafkaListener(topics = "user-synchronize")
    public void userSynchronize(@Header(value = "Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String tokenValue = authHeader.substring(7);
            try {
                Jwt jwt = jwtDecoder.decode(tokenValue);
                userSynchronizer.synchronizeWithIdp(jwt);
            } catch (JwtException e) {
                log.error("Failed to decode JWT token", e);
            }
        } else {
            log.warn("No valid Authorization header found");
        }
    }
}
