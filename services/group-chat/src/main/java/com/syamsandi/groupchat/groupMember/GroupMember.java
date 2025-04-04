package com.syamsandi.groupchat.groupMember;

import com.syamsandi.groupchat.common.BaseAuditingEntity;
import com.syamsandi.groupchat.groupChat.GroupChat;
import com.syamsandi.groupchat.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "group_member")
public class GroupMember extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupChat groupChat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    private LocalDateTime joinDate;

    private Long lastReadMessageId;

    @Transient
    public Long getUnreadMessageCount() {
        //todo query in database for performance
        if (lastReadMessageId == null) {
            return (long) groupChat.getMessages().size();
        }
        return groupChat.getMessages()
                .stream()
                .filter(m -> m.getId() > lastReadMessageId)
                .count();
    }
}
