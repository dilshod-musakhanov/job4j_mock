package ru.checkdev.notification.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Дильшод Мусаханов
 * @since 12.11.2023
 */
@Data
@NoArgsConstructor
@Entity(name = "chat_id")
public class ChatId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tg_chat_id")
    private String chatId;

    @Column(name = "profile_id")
    private String profileId;

    private String username;

    private String email;

    public ChatId(String chatId, String profileId, String username, String email) {
        this.chatId = chatId;
        this.profileId = profileId;
        this.username = username;
        this.email = email;
    }
}
