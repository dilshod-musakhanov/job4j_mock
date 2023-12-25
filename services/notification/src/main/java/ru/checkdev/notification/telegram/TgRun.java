package ru.checkdev.notification.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.checkdev.notification.service.SubscribeCategoryService;
import ru.checkdev.notification.telegram.action.*;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 3. Мидл
 * Инициализация телеграм бот,
 * username = берем из properties
 * token = берем из properties
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@Component
@Slf4j
public class TgRun {
    private final TgAuthCallWebClint tgAuthCallWebClint;
    private final ChatIdService chatIdService;
    private final TgConfig tgConfig;
    private final RegAction regAction;
    private final SubscribeCategoryService subscribeCategoryService;
    @Value("${tg.username}")
    private String username;
    @Value("${tg.token}")
    private String token;
    @Value("${server.site.url.login}")
    private String urlSiteAuth;

    @Autowired
    public TgRun(
            TgAuthCallWebClint tgAuthCallWebClint,
            ChatIdService chatIdService,
            TgConfig tgConfig,
            RegAction regAction,
            SubscribeCategoryService subscribeCategoryService) {
        this.tgAuthCallWebClint = tgAuthCallWebClint;
        this.chatIdService = chatIdService;
        this.tgConfig = tgConfig;
        this.regAction = regAction;
        this.subscribeCategoryService = subscribeCategoryService;
    }

    @PostConstruct
    public void initTg() {
        Map<String, Action> actionMap = Map.of(
                "/start", new InfoAction(List.of(
                        "/start", "/new", "/check", "/forget", "/subscribe", "/unsubscribe")),
                "/new", new RegAction(tgConfig, tgAuthCallWebClint, chatIdService, urlSiteAuth),
                "/check", new CheckAction(chatIdService),
                "/forget", new ForgetAction(tgConfig, tgAuthCallWebClint, chatIdService),
                "/subscribe", new SubscribeAction(subscribeCategoryService, chatIdService, tgAuthCallWebClint),
                "/unsubscribe", new UnsubscribeAction(subscribeCategoryService, chatIdService)
        );
        try {
            BotMenu menu = new BotMenu(actionMap, username, token, regAction, chatIdService);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(menu);
        } catch (TelegramApiException e) {
            log.error("Telegram bot: {}, ERROR {}", username, e.getMessage());
        }
    }
}
