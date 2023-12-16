package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Дильшод Мусаханов
 * @since 12.09.2023
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String URL_AUTH_REGISTRATION = "/registration";
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
    private final ChatIdService chatIdService;
    private final String urlSiteAuth;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Для регистрации введите имя пользователя и email разделенные хэштегом '#' без пробела.\n"
                + "Пример: username#example@example.com";
        return new SendMessage(chatId, text);
    }

    /**
     * Метод формирует ответ пользователю.
     * Весь метод разбит на 4 этапа проверки.
     * 1. Проверка на соответствие формату Email введенного текста.
     * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
     * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
     * 3.1 ответ при ошибке регистрации
     * 3.2 ответ при успешной регистрации.
     *
     * @param message Входящее сообщение от пользователя.
     * @return BotApiMethod<Message> - объект, представляющий ответ бота.
     */
    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var sl = System.lineSeparator();
        var text = "";
        var chatIdOptional = chatIdService.findByChatId(chatId);
        if (chatIdOptional.isPresent()) {
            text = "Ошибка регистрации. Регистрация по данному аккаунту Telegram уже существует" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        var credentials = message.getText();
        Map<String, String> map = tgConfig.credentials(credentials);
        if (map.containsKey("error")) {
            text = map.get("error");
            log.error(text);
            return new SendMessage(chatId, text + " Попробуйте снова." + sl + "/new");
        }
        var username = map.get("username");
        var email = map.get("email");
        var password = tgConfig.getPassword();
        var person = new PersonDTO(username, email, password, true, null,
                Calendar.getInstance());
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_REGISTRATION, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        var mapObject = tgConfig.getObjectToMap(result);
        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка регистрации: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }
        Map<String, Object> personMap = (Map<String, Object>) result;
        Map<String, Object> personDetailsMap = (Map<String, Object>) personMap.get("person");
        var profileId = personDetailsMap.get("id").toString();
        Optional<ChatId> optionalChatId = chatIdService.save(new ChatId(chatId, profileId, username, email));
        if (optionalChatId.isEmpty()) {
            log.error(String.format("Unable to save ChatId: %s, %s, %s, %s", chatId, profileId, username, email));
        }
        text = "Вы зарегистрированы: " + sl
                + "Имя пользователя: " + username + sl
                + "Логин: " + email + sl
                + "Пароль: " + password + sl
                + urlSiteAuth;
        return new SendMessage(chatId, text);
    }
}