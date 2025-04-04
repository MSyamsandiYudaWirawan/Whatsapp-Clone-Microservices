package com.syamsandi.groupchat.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {
    private final UserRepository repository;
    private final UserMapper mapper;

    public void synchronizeWithIdp(Jwt token) {
        getUserEmail(token).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        User user = mapper.toUser(token.getClaims());
        repository.save(user);
    }

    private Optional<String> getUserEmail(Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if(claims.containsKey("email")) {
            return Optional.of(claims.get("email").toString());
        }
        return Optional.empty();
    }
}