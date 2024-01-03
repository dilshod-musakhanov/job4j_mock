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
import static org.mockito.Mockito.*;

/**
 * @author Дильшод Мусаханов
 * @since 3.1.2024
 */
@ExtendWith(MockitoExtension.class)
class UnsubscribeActionTest {

    @Mock
    private ChatIdService chatIdService;

    @InjectMocks
    private UnsubscribeAction unsubscribeAction;

    @Test
    void whenUnsubscribeThenNotifySetFalse() {
        String chatId = "123456";
        String profileId = "1";
        String username = "username";
        String email = "email@email.com";
        Boolean completed = false;
        Boolean notify = true;
        ChatId chatIdObj = new ChatId(chatId, profileId, username, email, completed, notify);
        when(chatIdService.findByChatId(chatId)).thenReturn(Optional.of(chatIdObj));
        BotApiMethod<Message> result = unsubscribeAction.handle(createMockMessage(chatId));
        SendMessage sendMessage = (SendMessage) result;
        assertThat(result).isInstanceOf(SendMessage.class);
        String expectedText = "Вы отписались от уведомлений";
        assertThat(sendMessage.getText()).isEqualTo(expectedText);
        verify(chatIdService, times(1)).save(argThat(savedChatIdObj ->
                !savedChatIdObj.isNotify() && savedChatIdObj.getChatId().equals(chatId)
        ));
    }

    private Message createMockMessage(String chatId) {
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(Long.valueOf(chatId));
        message.setChat(chat);
        return message;
    }
}