package com.syamsandi.chat.chat;

import com.syamsandi.chat.common.BaseAuditingEntity;
import com.syamsandi.chat.message.Message;
import com.syamsandi.chat.message.MessageState;
import com.syamsandi.chat.message.MessageType;
import com.syamsandi.chat.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat")
@Builder
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_CURRENT_USER_ID,
query = "SELECT DISTINCT c  FROM Chat c " +
        "WHERE c.sender.id = :currUserId " +
        "OR c.receiver.id = :currUserId " +
        "ORDER BY c.createdDate DESC")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_CURRENT_USER_ID_AND_OTHER_USER_ID,
        query = "SELECT DISTINCT c FROM Chat c " +
                "WHERE (c.sender.id = :currentUserId AND c.receiver.id = :otherUserId)" +
                "OR (c.sender.id = :otherUserId AND c.receiver.id = :currentUserId)")
public class Chat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    @OrderBy("createdDate DESC")
    private List<Message> messages;

    @Transient
    public String getChatName(final String currUserId) {
        if (receiver.getId().equals(currUserId)) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return receiver.getFirstName() + " " + receiver.getLastName();
    }

    @Transient
    public Long getUnreadMessages(final String currUserId) {
        return messages
                .stream()
                .filter(m -> m.getReceiverId().equals(currUserId))
                .filter(m -> m.getState().equals(MessageState.SENT))
                .count();
    }

    @Transient
    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            if (!messages.getFirst().getType().equals(MessageType.TEXT)){
                return "Attachment";
            }
            return messages.getFirst().getContent();
        }
        return null;
    }
    @Transient
    public LocalDateTime getLastMessageTime() {
        if(messages != null && !messages.isEmpty()){
            return messages.getFirst().getCreatedDate();
        }
        return null;
    }
}
