package com.syamsandi.groupchat.groupMessage;

import com.syamsandi.groupchat.common.BaseAuditingEntity;
import com.syamsandi.groupchat.groupChat.GroupChat;
import com.syamsandi.groupchat.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "group_message")
public class GroupMessage extends BaseAuditingEntity {

    @Id
    @SequenceGenerator(name = "group_msg_seq", sequenceName = "group_msg_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_msg_seq")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupChat groupChat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private String mediaFilePath;

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    private GroupMessage replyTo;

    @OneToMany(mappedBy = "replyTo", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<GroupMessage> replies = new ArrayList<>();
}
