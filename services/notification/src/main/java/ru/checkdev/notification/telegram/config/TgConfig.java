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

    public Map<String, String> checkUsername(String username) {
        var name = username.trim();
        Map<String, String> map = new HashMap<>();
        if (name.length() < 2) {
            map.put("error", "Ошибка! Имя пользователя должно содержать 2 или более символов.");
            return map;
        }
        if (isEmail(name)) {
            map.put("error", "Ошибка! Ввели еmail вместо имя пользователя.");
            return map;
        }
        map.put("username", name);
        return map;
    }

    public Map<String, String> checkEmail(String userEmail) {
        var email = userEmail.trim();
        Map<String, String> map = new HashMap<>();
        if (!isEmail(email)) {
            map.put("error", String.format("Ошибка! Email: %s не корректный.", email));
            return map;
        }
        map.put("email", email);
        return map;
    }

    private boolean hasHashtag(String usernameAndEmail) {
        return usernameAndEmail.contains("#");
    }
}
