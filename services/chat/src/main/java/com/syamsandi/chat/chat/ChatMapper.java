package com.syamsandi.chat.chat;

import org.springframework.stereotype.Service;

@Service
public class ChatMapper {

    public ChatResponse toChatResponse(Chat chat, String currentUserId) {
        boolean isOtherUserOnline = false;

        if (chat.getSender().getId().equals(currentUserId)) {
            isOtherUserOnline = chat.getReceiver().isUserOnline();
        } else {
            isOtherUserOnline = chat.getSender().isUserOnline();
        }

        return new ChatResponse(
                chat.getId(),
                chat.getChatName(currentUserId),
                chat.getUnreadMessages(currentUserId),
                chat.getLastMessage(),
                chat.getLastMessageTime(),
                isOtherUserOnline,
                chat.getSender().getId(),
                chat.getReceiver().getId()
        );

    }
}