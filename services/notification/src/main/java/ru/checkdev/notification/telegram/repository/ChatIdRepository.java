package ru.checkdev.notification.telegram.repository;

import org.springframework.data.repository.CrudRepository;
import ru.checkdev.notification.domain.ChatId;

public interface ChatIdRepository extends CrudRepository<ChatId, Integer> {
    ChatId findByChatId(String chatId);
}
