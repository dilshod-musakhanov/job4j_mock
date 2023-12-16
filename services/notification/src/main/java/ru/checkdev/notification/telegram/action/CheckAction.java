package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.telegram.service.ChatIdService;

/**
 * Класс реализует пункт меню проверки существующего пользователя в телеграм бот
 * @author Дильшод Мусаханов
 * @since 12.11.2023
 */
@AllArgsConstructor
public class CheckAction implements Action {

    private final ChatIdService chatIdService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var chatIdOptional  = chatIdService.findByChatId(chatId);
        if (chatIdOptional.isEmpty()) {
            var text = "Данный аккаунт Telegram на сайте не зарегистрирован";
            return new SendMessage(chatId, text);
        }
        var username = chatIdOptional.get().getUsername();
        var email = chatIdOptional.get().getEmail();
        var text = String.format("Имя пользователя: %s\nЛогин: %s", username, email);
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
