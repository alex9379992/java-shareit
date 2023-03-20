package ru.yandex.practicum.shareIt.user;

import ru.yandex.practicum.shareIt.exeptions.BadRequestException;
import ru.yandex.practicum.shareIt.exeptions.ValidationException;

import java.util.Map;

public class UserValidator {


    public static boolean validation(User user, Map<Integer, User> userMap) {
        return validationName(user) && validationEmail(user, userMap);
    }

    private static boolean validationName(User user) {
        return !user.getName().isEmpty();
    }

    public static boolean validationEmail(User user, Map<Integer, User> userMap) {
        if (user.getEmail() == null) {
            throw new BadRequestException("Ошибка маппинга");
        } else {
            for (User checkUser : userMap.values()) {
                if (checkUser.getEmail().equals(user.getEmail()) && user.getId() != checkUser.getId()) {
                    throw new ValidationException("Ошибка валидации: пользователь с данной почтой уже существует");
                }
            }
            return true;
        }
    }

    public static boolean isThereAUser(int userId, Map<Integer, User> userMap) {
        return userMap.containsKey(userId);
    }
}
