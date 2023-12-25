package ru.checkdev.notification.telegram.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.repository.ChatIdRepository;

import java.util.Optional;

/**
 * @author Дильшод Мусаханов
 * @since 12.11.2023
 */
@Service
@AllArgsConstructor
@Slf4j
public class ChatIdService {

    private final ChatIdRepository chatIdRepository;

    public Optional<ChatId> save(ChatId chatId) {
        ChatId result = this.chatIdRepository.save(chatId);
        return Optional.ofNullable(result);
    }

    public Optional<ChatId> findByChatId(String chatId) {
        ChatId result = this.chatIdRepository.findByChatId(chatId);
        return Optional.ofNullable(result);
    }

    public boolean isCompleted(String chatId) {
        ChatId result = this.chatIdRepository.findByChatId(chatId);
        if (result == null) {
            return false;
        }
        return result.isCompleted();
    }

    public boolean hasUsername(String chatId) {
        ChatId result = this.chatIdRepository.findByChatId(chatId);
        if (result == null) {
            return false;
        }
        return result.getUsername() != null;
    }

    public void delete(String chatId) {
        Optional<ChatId> chatIdObj = findByChatId(chatId);
        chatIdRepository.delete(chatIdObj.get());
    }

    public boolean isPresent(String chatId) {
        return findByChatId(chatId).isPresent();
    }
}
