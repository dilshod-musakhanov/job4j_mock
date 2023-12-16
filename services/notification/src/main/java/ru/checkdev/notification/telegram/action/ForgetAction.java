package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Класс реализует пункт меню восстановления пароля пользователя в телеграм бот
 * @author Дильшод Мусаханов
 * @since 12.11.2023
 */
@AllArgsConstructor
@Slf4j
public class ForgetAction implements Action {

    private static final String URL_AUTH_FORGOT = "/forgot";
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
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
        var username = chatIdOptional.get().getUsername();
        var email = chatIdOptional.get().getEmail();
        var password = tgConfig.getPassword();
        var person = new PersonDTO(username, email, password, true, null,
                Calendar.getInstance());
        var text = "";
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_FORGOT, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        Map<String, Object> resultMap = (Map<String, Object>) result;
        String status = (String) resultMap.get("ok");
        if ("ok".equals(status)) {
            text = String.format("Новый пароль: %s", password);
            return new SendMessage(chatId, text);
        } else {
            log.error(String.format("Unable to get a new password for %s", chatId));
            text = "Ошибка при восстановлении пароля попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
