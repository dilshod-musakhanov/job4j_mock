package ru.checkdev.notification.telegram.action;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Дильшод Мусаханов
 * @since 12.12.2023
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegActionTest {

    @Mock
    private TgConfig tgConfig;

    @Mock
    private TgAuthCallWebClint authCallWebClint;

    @Mock
    private ChatIdService chatIdService;

    @InjectMocks
    private RegAction regAction;


    @Test
    public void whenHandleShouldReturnInitialMessage() {
        String chatId = "123456";
        Message message = createMockMessage(chatId);
        BotApiMethod<Message> result = regAction.handle(message);
        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Для регистрации введите имя пользователя:");

    }

    @Test
    public void whenChatIdAlreadyRegisteredThenReturnErrorMessage() {
        String chatId = "123456";
        when(chatIdService.isCompleted(chatId)).thenReturn(true);
        Message message = createMockMessage(chatId);
        BotApiMethod<Message> result = regAction.handle(message);
        Assertions.assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Ошибка регистрации. Регистрация по данному аккаунту Telegram уже существует");
    }

    @Test
    public void whenRegisteredSuccessfully() {
        String chatId = "123456";
        String username = "testUsername";
        ChatId chatIdWithUsername = new ChatId();
        chatIdWithUsername.setChatId(chatId);
        chatIdWithUsername.setUsername(username);

        Message message = createMockMessage(chatId);
        when(chatIdService.isCompleted(chatId)).thenReturn(false);
        when(chatIdService.hasUsername(chatId)).thenReturn(false);

        when(tgConfig.checkUsername(any())).thenReturn(Collections.singletonMap("username", "testUsername"));
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(new ChatId()));
        when(chatIdService.isCompleted(chatId)).thenReturn(false);
        when(chatIdService.hasUsername(chatId)).thenReturn(true);

        when(tgConfig.checkUsername(any())).thenReturn(Collections.singletonMap("username", "testUsername"));
        when(tgConfig.checkEmail(any())).thenReturn(Map.of("email", "test@example.com"));
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(new ChatId()));
        when(authCallWebClint.doPost(any(), any())).thenReturn(createMockSuccessResponse());
        when(tgConfig.getPassword()).thenReturn("testPassword");
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(chatIdWithUsername));

        BotApiMethod<Message> result = regAction.callback(message);
        var sl = System.lineSeparator();
        SendMessage sendMessage = (SendMessage) result;
        String expectedText = "Вы зарегистрированы: " + sl
                + "Имя пользователя: testUsername" + sl
                + "Логин: test@example.com" + sl
                + "Пароль: testPassword" + sl
                + "null";
        String actualText = sendMessage.getText().replaceAll("\\r\\n|\\r|\\n", sl);
        assertThat(actualText).isEqualTo(expectedText);
    }

    private Message createMockMessage(String chatId) {
        Message message = new Message();
        Chat chatObj = new Chat();
        chatObj.setId(Long.valueOf(chatId));
        message.setChat(chatObj);
        message.setText("Test message");
        return message;
    }


    private Mono<Object> createMockSuccessResponse() {
        return Mono.just(Map.of(
                "person", Map.of(
                        "id", "123",
                        "username", "testUsername"
                )
        ));
    }
}