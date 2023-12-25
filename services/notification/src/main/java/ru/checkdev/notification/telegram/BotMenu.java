package ru.checkdev.notification.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.checkdev.notification.telegram.action.Action;
import ru.checkdev.notification.telegram.action.RegAction;
import ru.checkdev.notification.telegram.service.ChatIdService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 3. Мидл
 * Реализация меню телеграм бота.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Дильшод Мусаханов
 * @since 12.09.2023
 */
public class BotMenu extends TelegramLongPollingBot {
    private final Map<String, String> bindingBy = new ConcurrentHashMap<>();
    private final Map<String, Action> actions;
    private final String username;
    private final String token;
    private final RegAction regAction;
    private final ChatIdService chatIdService;

    public BotMenu(Map<String, Action> actions, String username, String token, RegAction regAction, ChatIdService chatIdService) throws TelegramApiException {
        this.actions = actions;
        this.username = username;
        this.token = token;
        this.regAction = regAction;
        this.chatIdService = chatIdService;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var key = update.getMessage().getText();
            var chatId = update.getMessage().getChatId().toString();
            var sl = System.lineSeparator();

            if (!actions.containsKey(key) && chatIdService.isCompleted(chatId)) {
                var message = "Команда не поддерживается! Список доступных команд:" + sl
                        + "/start";
                SendMessage invalidCommand = new SendMessage(chatId, message);
                bindingBy.remove(chatId);
                send(invalidCommand);
                return;
            }

            if (actions.containsKey(key)) {
                var message = actions.get(key).handle(update.getMessage());
                bindingBy.put(chatId, key);
                send(message);
                return;
            }

            if (bindingBy.containsKey(chatId) && chatIdService.isPresent(chatId)) {
                var message = regAction.callback(update.getMessage());
                System.out.println(chatId);
                send(message);
                return;
            }

            if (!actions.containsKey(key) && bindingBy.size() > 0) {
                var message = "Команда не поддерживается! Список доступных команд:" + sl
                        + "/start";
                SendMessage invalidCommand = new SendMessage(chatId, message);
                bindingBy.remove(chatId);
                send(invalidCommand);
                return;
            }

            if (chatIdService.isPresent(chatId)) {
                var message = regAction.callback(update.getMessage());
                System.out.println(chatId);
                send(message);
                return;
            }

            if (bindingBy.containsKey(chatId)) {
                var message = actions.get(bindingBy.get(chatId)).callback(update.getMessage());
                bindingBy.remove(chatId);
                send(message);
            } else {
                var message = "Команда не поддерживается! Список доступных команд:" + sl
                        + "/start";
                SendMessage invalidCommand = new SendMessage(chatId, message);
                send(invalidCommand);
            }
        }
    }

    private void send(BotApiMethod msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
