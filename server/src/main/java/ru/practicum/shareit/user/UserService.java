package ru.practicum.shareit.user;



import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto patchUser(UserDto userDto, long userId);

    UserDto getUserDto(long userId);

    void deleteUser(long userId);

    List<UserDto> getUsers();
}
