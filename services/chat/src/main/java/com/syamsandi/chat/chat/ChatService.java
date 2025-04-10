package com.syamsandi.chat.chat;

import com.syamsandi.chat.exception.ChatExceptions.ChatServiceException;
import com.syamsandi.chat.exception.ChatExceptions.UserNotFoundException;
import com.syamsandi.chat.user.User;
import com.syamsandi.chat.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Transactional(readOnly = true)
    public CompletableFuture<List<ChatResponse>> getChatByCurrentUserIdAsync(Authentication currentUser) {
        final String currentUserId = currentUser.getName();
        return CompletableFuture.supplyAsync(() ->
                                chatRepository.findChatByCurrentUserId(currentUserId)
                                        .stream()
                                        .map(c -> chatMapper.toChatResponse(c, currentUserId))
                                        .toList(),
                        executor)
                .exceptionally(ex -> {
                    throw new ChatServiceException("Failed to fetch chats", ex);
                });
    }

    public CompletableFuture<String> createChatAsync(String currentUserId, String otherUserId) {
        return CompletableFuture.supplyAsync(() ->
                                chatRepository.findChatByCurrentUserIdAndOtherUserId(currentUserId, otherUserId)
                                        .map(Chat::getId)
                                        .orElse(null),
                        executor)
                .thenComposeAsync(existingChatId -> {
                    if (existingChatId != null) {
                        return CompletableFuture.completedFuture(existingChatId);
                    }

                    CompletableFuture<User> currentUserFuture = getUserAsync(currentUserId);
                    CompletableFuture<User> otherUserFuture = getUserAsync(otherUserId);

                    return currentUserFuture.thenCombineAsync(otherUserFuture, (currentUser, otherUser) -> {
                        Chat chat = new Chat();
                        chat.setSender(currentUser);
                        chat.setReceiver(otherUser);
                        return chatRepository.save(chat).getId();
                    }, executor);
                }, executor)
                .orTimeout(5, TimeUnit.SECONDS) // Add timeout
                .exceptionally(ex -> {
                    throw new ChatServiceException("Failed to create chat", ex);
                });
    }

    private CompletableFuture<User> getUserAsync(String userId) {
        return CompletableFuture.supplyAsync(() ->
                        userRepository.findUserByPublicId(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId)),
                executor);
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatByCurrentUserId(Authentication currentUser) {
        final String currentUserId = currentUser.getName();
        return chatRepository.findChatByCurrentUserId(currentUserId)
                .stream()
                .map(c -> chatMapper.toChatResponse(c, currentUserId))
                .toList();
    }

    @Transactional
    public String createChat(String currentUserId, String otherUserId) {
        return chatRepository.findChatByCurrentUserIdAndOtherUserId(currentUserId, otherUserId)
                .map(Chat::getId)
                .orElseGet(() -> {
                    User currentUser = userRepository.findUserByPublicId(currentUserId)
                            .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserId));

                    User otherUser = userRepository.findUserByPublicId(otherUserId)
                            .orElseThrow(() -> new UserNotFoundException("User not found: " + otherUserId));

                    Chat chat = new Chat();
                    chat.setSender(currentUser);
                    chat.setReceiver(otherUser);
                    return chatRepository.save(chat).getId();
                });
    }
}


