package ru.yandex.practicum.shareIt.user;

import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto patchUser(UserDto userDto, long userId);

    UserDto getUserDto(long userId);

    void deleteUser(long userId);

    List<UserDto> getUsers();
}
