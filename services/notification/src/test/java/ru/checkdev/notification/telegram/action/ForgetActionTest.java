package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

/**
 * @author Дильшод Мусаханов
 * @since 12.12.2023
 */
@ExtendWith(MockitoExtension.class)
class ForgetActionTest {

    private static final String URL_AUTH_FORGOT = "/forgot";

    @Mock
    private TgAuthCallWebClint authCallWebClint;

    @Mock
    private ChatIdService chatIdService;

    @Mock
    private TgConfig tgConfig;

    @InjectMocks
    private ForgetAction forgetAction;

    @Test
    void whenUserNotRegistered() {
        String chatId = "123456";
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.empty());
        BotApiMethod<Message> result = forgetAction.handle(createMockMessage(chatId));
        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Данный аккаунт Telegram на сайте не зарегистрирован").contains("/start");
    }

    @Test
    void handleUserRegisteredSuccessfullyGetNewPassword() {
        String chatId = "123456";
        String profileId = "1";
        String username = "username";
        String email = "email@email.com";
        String password = "newPassword";
        ChatId chatIdObj = new ChatId(chatId, profileId, username, email);
        PersonDTO expectedPerson = new PersonDTO(
                username, email, password, true, null, Calendar.getInstance()
        );
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(chatIdObj));
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.just(Collections.singletonMap("ok", "ok")));
        when(tgConfig.getPassword()).thenReturn("newPassword");

        ArgumentCaptor<PersonDTO> argumentCaptor = ArgumentCaptor.forClass(PersonDTO.class);

        BotApiMethod<Message> result = forgetAction.handle(createMockMessage(chatId));
        verify(authCallWebClint).doPost(eq(URL_AUTH_FORGOT), argumentCaptor.capture());

        PersonDTO actualPerson = argumentCaptor.getValue();
        assertThat(actualPerson.getUsername()).isEqualTo(expectedPerson.getUsername());
        assertThat(actualPerson.getEmail()).isEqualTo(expectedPerson.getEmail());
        assertThat(actualPerson.getPassword()).isEqualTo(expectedPerson.getPassword());

        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Новый пароль: " + password);
    }

    @Test
    public void whenWebClientFailsGetException() {
        String chatId = "123456";
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(new ChatId()));
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.error(new RuntimeException("WebClient error")));
        when(tgConfig.getPassword()).thenReturn("newPassword");

        BotApiMethod<Message> result = forgetAction.handle(createMockMessage(chatId));

        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Сервис не доступен попробуйте позже").contains("/start");
    }

    @Test
    void whenUserRegisteredButWebClientReturnsNonOkStatus() {
        String chatId = "123456";
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(new ChatId()));
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.just(Collections.singletonMap("E-mail не найден.", "E-mail не найден.")));
        when(tgConfig.getPassword()).thenReturn("newPassword");
        BotApiMethod<Message> result = forgetAction.handle(createMockMessage(chatId));
        assertThat(result).isInstanceOf(SendMessage.class);
        SendMessage sendMessage = (SendMessage) result;
        assertThat(sendMessage.getText()).contains("Ошибка при восстановлении пароля попробуйте позже").contains("/start");
    }

    private Message createMockMessage(String chatId) {
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(Long.valueOf(chatId));
        message.setChat(chat);
        return message;
    }

}