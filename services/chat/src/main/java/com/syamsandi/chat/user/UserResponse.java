package com.syamsandi.chat.user;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime lastSeen,
        boolean isOnline
) {}

