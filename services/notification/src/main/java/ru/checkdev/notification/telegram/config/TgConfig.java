package ru.checkdev.notification.telegram.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3. Мидл
 * Класс дополнительных функций телеграм бота, проверка почты, генерация пароля.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@Component
public class TgConfig {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
    private static final String DEFAULT_PREFIX = "tg/";
    private static final int DEFAULT_PASS_SIZE = 8;

    private final String prefix;
    private final int passSize;

    public TgConfig() {
        this(DEFAULT_PREFIX, DEFAULT_PASS_SIZE);
    }

    public TgConfig(String prefix, int passSize) {
        this.prefix = prefix;
        this.passSize = passSize;
    }

    /**
     * Метод проверяет входящую строку на соответствие формату email
     *
     * @param email String
     * @return boolean
     */
    public boolean isEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * метод генерирует пароль для пользователя
     *
     * @return String
     */
    public String getPassword() {
        String password = prefix + UUID.randomUUID();
        return password.substring(0, passSize);
    }

    /**
     * Метод преобразовывает Object в карту Map<String,String>
     *
     * @param object Object or Person(Auth)
     * @return Map
     */
    public Map<String, String> getObjectToMap(Object object) {
        return MAPPER.convertValue(object, Map.class);
    }

    /**
     * Проверяет и извлекает имя пользователя и адрес электронной почты из заданной входной строки.
     *
     * @param usernameAndEmail Входная строка, содержащая имя пользователя и адрес электронной почты, разделенные хэштегом (#).
     * @return Карта, содержащая извлеченные имя пользователя и адрес электронной почты или сообщение об ошибке в случае неудачной проверки.
     * Структура карты:
     * - Ключ: «имя пользователя» - Извлеченное имя пользователя
     * - Ключ: «email» - Извлеченный адрес электронной почты.
     * - Ключ: «ошибка». Если проверка не удалась, появится сообщение об ошибке, описывающее проблему.
     */
    public Map<String, String> credentials(String usernameAndEmail) {
        Map<String, String> map = new HashMap<>();
        if (!hasHashtag(usernameAndEmail)) {
            map.put("error", "Ошибка! Отсутствует хэштег.");
            return map;
        }
        String[] parts = usernameAndEmail.split("#");
        if (parts.length != 2) {
            map.put("error", "Ошибка! Имя пользователя или Email отсутствуют или содержит более одного символа #.");
            return map;
        }
        String username = parts[0].trim();
        String email = parts[1].trim();

        if (username.matches(".*\\s.*") || email.matches(".*\\s.*")) {
            map.put("error", "Ошибка! Имя пользователя и Email не должны содержать пробелов.");
            return map;
        }

        if (username.length() < 2) {
            map.put("error", "Ошибка! Имя пользователя должно содержать 2 или более символов.");
            return map;
        }

        if (isEmail(username)) {
            map.put("error", "Ошибка! Email указывать после имени пользователя.");
            return map;
        }

        if (!isEmail(email)) {
            map.put("error", String.format("Ошибка! Email: %s не корректный.", email));
            return map;
        }
        map.put("username", username);
        map.put("email", email);
        return map;
    }

    private boolean hasHashtag(String usernameAndEmail) {
        return usernameAndEmail.contains("#");
    }
}
