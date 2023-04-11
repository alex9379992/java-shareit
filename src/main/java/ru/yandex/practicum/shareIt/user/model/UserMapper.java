package ru.yandex.practicum.shareIt.user.model;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.UserDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setId(user.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static Map<Long, User> mapToUsersMap(List<User> users) {
        return users.stream().collect(Collectors.toMap(User::getId, user -> user));
    }
}
