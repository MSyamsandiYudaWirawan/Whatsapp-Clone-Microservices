package com.syamsandi.groupchat.groupChat;

import com.syamsandi.groupchat.common.BaseAuditingEntity;
import com.syamsandi.groupchat.groupMember.GroupMember;
import com.syamsandi.groupchat.groupMessage.GroupMessage;
import com.syamsandi.groupchat.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "group_chat")
public class GroupChat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String description;

    private String avatarPath;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMember> members = new HashSet<>();

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdDate DESC")
    private List<GroupMessage> messages = new ArrayList<>();

    private int memberCount;
}
