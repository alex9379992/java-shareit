package ru.yandex.practicum.shareIt.user;

import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto patchUser(UserDto userDto, long userId);

    UserDto getUserDto(long userId);

    User getUser(long userId);

    void deleteUser(long userId);

    List<UserDto> getUsers();

    Map<Long, User> getUsersMap();
}
