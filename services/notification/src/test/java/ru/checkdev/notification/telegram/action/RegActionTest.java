package ru.checkdev.notification.telegram.action;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.Mockito.when;

/**
 * @author Дильшод Мусаханов
 * @since 12.12.2023
 */
@ExtendWith(MockitoExtension.class)
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
        assertThat(sendMessage.getText()).contains("Для регистрации введите имя пользователя и email разделенные хэштегом '#' без пробела.");

    }

    @Test
    public void whenChatIdAlreadyRegisteredThenReturnErrorMessage() {
        String chatId = "123456";
        String profileId = "1";
        String username = "username";
        String email = "email@email.com";
        ChatId chatIdObj = new ChatId(chatId, profileId, username, email);
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(chatIdObj));
        Message message = createMockMessage(chatId);
        BotApiMethod<Message> result = regAction.callback(message);
        Assertions.assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Ошибка регистрации. Регистрация по данному аккаунту Telegram уже существует");

    }

    @Test
    public void whenInvalidCredentialProvidedThenReturnErrorMessage() {
        String chatId = "123456";
        Message message = createMockMessage(chatId);
        when(authCallWebClint.doPost(any(), any())).thenThrow(new RuntimeException("Service unavailable"));
        BotApiMethod<Message> result = regAction.callback(message);
        Assertions.assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Сервис не доступен");
    }

    @Test
    void whenReturnSuccessMessageOnSuccessfulRegistration() {
        String chatId = "123456";
        Message message = createMockMessage(chatId);
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.empty());
        when(tgConfig.credentials(any())).thenReturn(Collections.emptyMap());
        Map<String, Object> mockPersonDetails = new HashMap<>();
        mockPersonDetails.put("id", "1");
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("ok", "ok");
        mockResult.put("person", mockPersonDetails);
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.just(mockResult));
        when(tgConfig.getObjectToMap(mockResult)).thenReturn(Collections.emptyMap());
        when(chatIdService.save(any())).thenReturn(Optional.of(new ChatId()));
        BotApiMethod<Message> result = regAction.callback(message);
        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Вы зарегистрированы");
    }

    private Message createMockMessage(String chatId) {
        Message message = new Message();
        Chat chatObj = new Chat();
        chatObj.setId(Long.valueOf(chatId));
        message.setChat(chatObj);
        message.setText("username#example@example.com");
        return message;
    }

}