package com.syamsandi.chat.user;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getLastSeen(),
                user.isUserOnline());
    }

    public User toUser(Map<String, Object> claims) {
        User user = new User();
        if(claims.containsKey("sub")) {
            user.setId(claims.get("sub").toString());
        }
        if(claims.containsKey("given_name")) {
            user.setFirstName(claims.get("given_name").toString());
        }else if(claims.containsKey("nickname")) {
            user.setFirstName(claims.get("nickname").toString());
        }
        if(claims.containsKey("family_name")) {
            user.setLastName(claims.get("family_name").toString());
        }
        if(claims.containsKey("email")) {
            user.setEmail(claims.get("email").toString());
        }
        user.setLastSeen(LocalDateTime.now());
        return user;
    }
}
