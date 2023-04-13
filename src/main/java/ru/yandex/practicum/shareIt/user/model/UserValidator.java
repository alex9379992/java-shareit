package ru.yandex.practicum.shareIt.user.model;

import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.UserDto;

import java.util.Map;

public class UserValidator {


    public static boolean validation(UserDto userDto) {
        return validationName(userDto) && validationEmail(userDto);
    }

    private static boolean validationName(UserDto userDto) {
        return !userDto.getName().isEmpty();
    }

    private static boolean validationEmail(UserDto userDto) {
        return !userDto.getEmail().isEmpty();
    }

    public static boolean isThereAUser(long userId, Map<Long, User> userMap) {
        return userMap.containsKey(userId);
    }
}
