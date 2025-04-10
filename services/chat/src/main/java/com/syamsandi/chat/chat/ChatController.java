package com.syamsandi.chat.chat;

import com.syamsandi.chat.common.StringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public CompletableFuture<ResponseEntity<StringResponse>> createChat(@RequestParam("sender-id") String senderId,
                                                     @RequestParam("receiver-id") String receiverId) {
        return chatService.createChatAsync(senderId, receiverId).thenApply(
                chatId -> {
                    StringResponse stringResponse = StringResponse.builder()
                            .response(chatId)
                            .build();
                    return ResponseEntity.ok(stringResponse);
                }
        );
    }

    @GetMapping
    private CompletableFuture<ResponseEntity<List<ChatResponse>>> getChatsByReceiver(Authentication authentication) {
        return chatService.getChatByCurrentUserIdAsync(authentication).thenApply(ResponseEntity::ok);
    }
}