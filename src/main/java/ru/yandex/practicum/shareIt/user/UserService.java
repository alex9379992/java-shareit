package ru.yandex.practicum.shareIt.user;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDto createUser(UserDto userDto);
    UserDto patchUser(UserDto userDto, long userId);
    UserDto getUser(long userId);
    void deleteUser(long userId);
    List<UserDto> getUsers();
    Map<Long, User> getUsersMap();
}
