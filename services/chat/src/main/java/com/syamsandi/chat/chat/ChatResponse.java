package com.syamsandi.chat.chat;

import java.time.LocalDateTime;

public record ChatResponse(
         String id,
         String name,
         long unreadCount,
         String lastMessage,
         LocalDateTime lastMessageTime,
         boolean isOtherUserOnline,
         String senderId,
         String receiverId
) {
}
