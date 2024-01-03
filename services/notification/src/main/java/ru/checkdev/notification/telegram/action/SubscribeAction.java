package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.telegram.service.ChatIdService;

@AllArgsConstructor
@Slf4j
public class SubscribeAction implements Action {

    private final ChatIdService chatIdService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var sl = System.lineSeparator();
        var chatIdOptional  = chatIdService.findByChatId(chatId);
        if (chatIdOptional.isEmpty()) {
            var text = "Данный аккаунт Telegram на сайте не зарегистрирован" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        var existingChatId = chatIdOptional.get();
        existingChatId.setNotify(true);
        chatIdService.save(existingChatId);
        var text = "Вы подписаны на уведомления";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return null;
    }
}
