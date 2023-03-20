package ru.yandex.practicum.shareIt.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserDto getUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
