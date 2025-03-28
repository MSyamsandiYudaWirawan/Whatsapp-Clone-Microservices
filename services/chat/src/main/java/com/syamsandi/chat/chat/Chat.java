package com.syamsandi.chat.chat;

import com.syamsandi.chat.common.BaseAuditingEntity;
import com.syamsandi.chat.message.Message;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat")
@Builder
public class Chat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String senderId;
    private String receiverId;
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
}
