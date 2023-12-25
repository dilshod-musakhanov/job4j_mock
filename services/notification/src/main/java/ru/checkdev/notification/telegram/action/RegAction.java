package ru.checkdev.notification.telegram.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Дильшод Мусаханов
 * @since 12.09.2023
 */
@Component
@Slf4j
public class RegAction implements Action {
    private static final String URL_AUTH_REGISTRATION = "/registration";
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
    private final ChatIdService chatIdService;
    private final String urlSiteAuth;

    public RegAction(
            TgConfig tgConfig,
            TgAuthCallWebClint authCallWebClint,
            ChatIdService chatIdService,
            @Value("${server.site.url.login}") String urlSiteAuth) {
        this.tgConfig = tgConfig;
        this.authCallWebClint = authCallWebClint;
        this.chatIdService = chatIdService;
        this.urlSiteAuth = urlSiteAuth;
    }

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var sl = System.lineSeparator();
        var text = "Для регистрации введите имя пользователя:";
        var status = chatIdService.isCompleted(chatId);
        if (status) {
            text = "Ошибка регистрации. Регистрация по данному аккаунту Telegram уже существует" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        var chatIdObj = new ChatId();
        chatIdObj.setChatId(chatId);
        chatIdService.save(chatIdObj);
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var input = message.getText();
        var sl = System.lineSeparator();
        var text = "Теперь введите ваш email:";
        if (chatIdService.isCompleted(chatId)) {
            text = "Ошибка регистрации. Регистрация по данному аккаунту Telegram уже существует" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        if (!chatIdService.hasUsername(chatId)) {
            Map<String, String> mapUsername = tgConfig.checkUsername(input);
            if (mapUsername.containsKey("error")) {
                text = mapUsername.get("error");
                log.error(text);
                chatIdService.delete(chatId);
                return new SendMessage(chatId, text + " Попробуйте снова." + sl + "/new");
            }
            var username = mapUsername.get("username");
            var chatIdObj = chatIdService.findByChatId(chatId).get();
            chatIdObj.setUsername(username);
            chatIdService.save(chatIdObj);
            return new SendMessage(chatId, text);
        }
        Map<String, String> mapEmail = tgConfig.checkEmail(input);
        if (mapEmail.containsKey("error")) {
            text = mapEmail.get("error");
            log.error(text);
            chatIdService.delete(chatId);
            return new SendMessage(chatId, text + " Попробуйте снова." + sl + "/new");
        }
        var username = chatIdService.findByChatId(chatId).get().getUsername();
        var email = mapEmail.get("email");
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
            chatIdService.delete(chatId);
            return new SendMessage(chatId, text);
        }
        Map<String, Object> personMap = (Map<String, Object>) result;
        Map<String, Object> personDetailsMap = (Map<String, Object>) personMap.get("person");
        var profileId = personDetailsMap.get("id").toString();
        var chatIdObj = chatIdService.findByChatId(chatId).get();
        chatIdObj.setProfileId(profileId);
        chatIdObj.setEmail(email);
        chatIdObj.setCompleted(true);
        chatIdService.save(chatIdObj);

        text = "Вы зарегистрированы: " + sl
                + "Имя пользователя: " + username + sl
                + "Логин: " + email + sl
                + "Пароль: " + password + sl
                + urlSiteAuth;

        return new SendMessage(chatId, text);
    }
}