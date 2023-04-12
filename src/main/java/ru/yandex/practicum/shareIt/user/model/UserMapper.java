package ru.yandex.practicum.shareIt.user.model;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);
    UserDto toUserDto(User user);

    default Map<Long, User> mapToUsersMap(List<User> users) {
        return users.stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    default List<UserDto> mapToUserDtoList(List<User> users) {
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }
}
