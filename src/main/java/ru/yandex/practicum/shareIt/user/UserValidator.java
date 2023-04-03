package ru.yandex.practicum.shareIt.user;

import ru.yandex.practicum.shareIt.exeptions.BadRequestException;
import ru.yandex.practicum.shareIt.exeptions.ValidationException;

import java.util.Map;

public class UserValidator {


    public static boolean validation(UserDto userDto, Map<Long, User> userMap) {
        return validationName(userDto) && validationEmail(userDto, userMap);
    }

    private static boolean validationName(UserDto userDto) {
        return !userDto.getName().isEmpty();
    }

    public static boolean validationEmail(UserDto userDto, Map<Long, User> userMap) {
        if (userDto.getEmail() == null) {
            throw new BadRequestException("Ошибка маппинга");
        } else {
            for (User checkUser : userMap.values()) {
                if (checkUser.getEmail().equals(userDto.getEmail()) && !userDto.getId().equals(checkUser.getId())) {
                    throw new ValidationException("Ошибка валидации: пользователь с данной почтой уже существует");
                }
            }
            return true;
        }
    }

    public static boolean isThereAUser(long userId, Map<Long, User> userMap) {
        return userMap.containsKey(userId);
    }
}
