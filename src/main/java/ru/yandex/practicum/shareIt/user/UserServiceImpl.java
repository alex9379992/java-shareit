package ru.yandex.practicum.shareIt.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareIt.mapper.Mapper;
import ru.yandex.practicum.shareIt.exeptions.UserNotFoundException;
import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.UserDto;


import javax.validation.Valid;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;

    @Override
    public UserDto createUser(@Valid UserDto userDto) {
        User user = userRepository.save(mapper.toUser(userDto));
        log.info("Новый пользователь создан с id " + user.getId());
        return mapper.toUserDto(user);
    }

    @Override
    public UserDto patchUser(UserDto userDto, long userId) {
        userDto.setId(userId);
        User user = findUserById(userId);
        userRepository.save(patcher(userDto, user));
        log.info("Информация о пользователе с id " + userId + " обновлена");
        return mapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserDto(long userId) {
        log.info("Отправлен пользователь с id " + userId);
        return mapper.toUserDto(findUserById(userId));
    }


    @Override
    public void deleteUser(long userId) {
        User user = findUserById(userId);
        log.info("Пользователь с id " + userId + " удален");
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        log.info("Сформирован и отправлен список пользователей");
        return mapper.mapToUserDtoList(userRepository.findAll());
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + id + " не найден"));
    }

    @Override
    public User patcher(UserDto userDto, User user) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}
