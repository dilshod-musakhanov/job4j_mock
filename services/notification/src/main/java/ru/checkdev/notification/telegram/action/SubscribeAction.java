package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.CategoryDTO;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.service.SubscribeCategoryService;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.telegram.service.TgCategoryCallWebClient;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class SubscribeAction implements Action {

    private static final String URL_CATEGORIES = "/categories/";
    private final SubscribeCategoryService subscribeCategoryService;
    private final ChatIdService chatIdService;
    private final TgCategoryCallWebClient tgCategoryCallWebClient;

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
        Object result;
        try {
            result = tgCategoryCallWebClient.doGetCategories(URL_CATEGORIES).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            var text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }
        List<CategoryDTO> categoryDTOList = (List<CategoryDTO>) result;
        var profileId = Integer.parseInt(chatIdOptional.get().getProfileId());

        Set<Integer> categoryIds = new CopyOnWriteArraySet<>(
                categoryDTOList.stream()
                        .map(CategoryDTO::getId)
                        .collect(Collectors.toSet())
        );

        List<Integer> allProfileCategories = subscribeCategoryService.findCategoriesByUserId(profileId);
        categoryIds.addAll(allProfileCategories);

        for (Integer id : categoryIds) {
            var subscribeCategory = new SubscribeCategory();
            subscribeCategory.setUserId(profileId);
            subscribeCategory.setCategoryId(id);
            subscribeCategoryService.save(subscribeCategory);
        }

        var text = "Вы подписаны на уведомления";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return null;
    }
}
