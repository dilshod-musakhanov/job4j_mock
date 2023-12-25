package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.service.ChatIdService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Дильшод Мусаханов
 * @since 12.12.2023
 */
@ExtendWith(MockitoExtension.class)
class CheckActionTest {

    @Mock
    private ChatIdService chatIdService;

    @InjectMocks
    private CheckAction checkAction;

    @Test
    void whenUserNotRegistered() {
        String chatId = "123456";
        var sl = System.lineSeparator();
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.empty());
        BotApiMethod<Message> result = checkAction.handle(createMockMessage(chatId));
        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        var text = "Данный аккаунт Telegram на сайте не зарегистрирован" + sl
                + "/start";
        assertThat(sendMessage.getText()).isEqualTo(text);
    }

    @Test
    void whenUserRegistered() {
        String chatId = "123456";
        String profileId = "1";
        String username = "username";
        String email = "email@email.com";
        Boolean completed = false;
        ChatId chatIdObj = new ChatId(chatId, profileId, username, email, completed);
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(chatIdObj));
        BotApiMethod<Message> result = checkAction.handle(createMockMessage(chatId));
        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        String expectedText = String.format("Имя пользователя: %s\nЛогин: %s", username, email);
        assertThat(sendMessage.getText()).isEqualTo(expectedText);
    }

    private Message createMockMessage(String chatId) {
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(Long.valueOf(chatId));
        message.setChat(chat);
        return message;
    }
}