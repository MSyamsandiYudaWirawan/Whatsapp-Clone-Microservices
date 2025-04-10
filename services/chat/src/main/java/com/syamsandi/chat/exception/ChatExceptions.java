package com.syamsandi.chat.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;

import java.util.Map;

public final class ChatExceptions {

    private ChatExceptions() {}


    @Getter
    public static class ChatServiceException extends RuntimeException {
        private final String errorCode;
        private final Map<String, Object> details;

        public ChatServiceException(String message, Throwable cause) {
            super(message, cause);
            this.errorCode = "CHAT-000";
            this.details = null;
        }
    }

    @Getter
    public static class UserNotFoundException extends EntityNotFoundException {
        private final Long userId;

        public UserNotFoundException(String message) {
            super(message);
            this.userId = null;
        }
    }
}