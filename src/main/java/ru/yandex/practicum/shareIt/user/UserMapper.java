package ru.yandex.practicum.shareIt.user;

import org.springframework.stereotype.Component;

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
}
