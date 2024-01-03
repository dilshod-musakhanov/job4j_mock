package ru.checkdev.notification.telegram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.repository.ChatIdRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Дильшод Мусаханов
 * @since 12.11.2023
 */
@ExtendWith(MockitoExtension.class)
class ChatIdServiceTest {
    @Mock
    private ChatIdRepository chatIdRepository;

    @InjectMocks
    private ChatIdService chatIdService;

    @Test
    public void whenSaveChatIdSuccessfully() {
        ChatId chatId = new ChatId("123456", "1", "username", "email@email.com", false, false);
        when(chatIdRepository.save(chatId)).thenReturn(chatId);
        Optional<ChatId> savedChatId = chatIdService.save(chatId);
        assertThat(savedChatId).isPresent();
        assertThat(savedChatId.get()).isEqualTo(chatId);
        verify(chatIdRepository, times(1)).save(chatId);
    }

    @Test
    void whenFindByChatIdExists() {
        ChatId chatId = new ChatId("123456", "1", "username", "email@email.com", false, false);
        when(chatIdRepository.findByChatId(chatId.getChatId())).thenReturn(chatId);
        Optional<ChatId> savedChatId = chatIdService.findByChatId(chatId.getChatId());
        assertThat(savedChatId).isPresent();
        assertThat(savedChatId.get()).isEqualTo(chatId);
    }

    @Test
    void whenFindByChatIdNotExist() {
        String chatId = "123456";
        when(chatIdRepository.findByChatId(chatId)).thenReturn(null);
        Optional<ChatId> optionalChatId = chatIdService.findByChatId(chatId);
        assertThat(optionalChatId).isEmpty();
        verify(chatIdRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void whenIsCompletedTrue() {
        String chatId = "123456";
        ChatId chatIdObj = new ChatId(chatId, "1", "username", "email@email.com", true, false);
        when(chatIdRepository.findByChatId(chatId)).thenReturn(chatIdObj);

        boolean result = chatIdService.isCompleted(chatId);

        assertThat(result).isTrue();
        verify(chatIdRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void whenIsCompletedFalse() {
        String chatId = "123456";
        ChatId chatIdObj = new ChatId(chatId, "1", "username", "email@email.com", false, false);
        when(chatIdRepository.findByChatId(chatId)).thenReturn(chatIdObj);

        boolean result = chatIdService.isCompleted(chatId);

        assertThat(result).isFalse();
        verify(chatIdRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void whenHasUsernameTrue() {
        String chatId = "123456";
        ChatId chatIdObj = new ChatId(chatId, "1", "username", "email@email.com", false, false);
        when(chatIdRepository.findByChatId(chatId)).thenReturn(chatIdObj);

        boolean result = chatIdService.hasUsername(chatId);

        assertThat(result).isTrue();
        verify(chatIdRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void whenHasUsernameFalse() {
        String chatId = "123456";
        ChatId chatIdObj = new ChatId(chatId, "1", null, "email@email.com", false, false);
        when(chatIdRepository.findByChatId(chatId)).thenReturn(chatIdObj);

        boolean result = chatIdService.hasUsername(chatId);

        assertThat(result).isFalse();
        verify(chatIdRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void whenDeleteChatId() {
        String chatId = "123456";
        ChatId chatIdObj = new ChatId(chatId, "1", "username", "email@email.com", false, false);
        when(chatIdRepository.findByChatId(chatId)).thenReturn(chatIdObj);

        chatIdService.delete(chatId);

        verify(chatIdRepository, times(1)).delete(chatIdObj);
    }

    @Test
    void whenIsPresentTrue() {
        String chatId = "123456";
        ChatId chatIdObj = new ChatId(chatId, "1", "username", "email@email.com", false, false);
        when(chatIdRepository.findByChatId(chatId)).thenReturn(chatIdObj);

        boolean result = chatIdService.isPresent(chatId);

        assertThat(result).isTrue();
        verify(chatIdRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void whenIsPresentFalse() {
        String chatId = "123456";
        when(chatIdRepository.findByChatId(chatId)).thenReturn(null);

        boolean result = chatIdService.isPresent(chatId);

        assertThat(result).isFalse();
        verify(chatIdRepository, times(1)).findByChatId(chatId);
    }

}