package ru.yandex.practicum.shareIt.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.exeptions.ValidationException;
import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.UserDto;
import ru.yandex.practicum.shareIt.user.model.UserMapper;
import ru.yandex.practicum.shareIt.user.model.UserValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(@Valid UserDto userDto) {
        if (UserValidator.validation(userDto)) {
            User user = userRepository.save(userMapper.toUser(userDto));
            log.info("Новый пользователь создан с id " + user.getId());
            return userMapper.toUserDto(user);
        } else {
            log.warn("Ошибка валидации");
            throw new ValidationException("Ошибка валидации");
        }
    }

    @Override
    public UserDto patchUser(UserDto userDto, long userId) {
        userDto.setId(userId);
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            User user = userRepository.getById(userId);
            userRepository.save(patcher(userDto, user));
            log.info("Информация о пользователе с id " + userId + " обновлена");
            return userMapper.toUserDto(user);
        } else {
            log.warn("Пользователь " + userId + " не найден");
            throw new SearchException("Пользователь " + userId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserDto(long userId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            log.info("Отправлен пользователь с id " + userId);
            return userMapper.toUserDto(getUserById(userId));
        } else {
            log.warn("Пользователь " + userId + " не найден");
            throw new SearchException("Пользователь " + userId + " не найден");
        }
    }


    @Override
    public void deleteUser(long userId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            log.info("Пользователь с id " + userId + " удален");
            userRepository.deleteById(userId);
        } else {
            log.warn("Пользователь " + userId + " не найден");
            throw new SearchException("Пользователь " + userId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        log.info("Сформирован и отправлен список пользователей");
        return userMapper.mapToUserDtoList(userRepository.findAll());
    }

    private User getUserById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            log.warn("Обьекта не существует");
            throw new NullPointerException("Обьекта не существует");
        }
    }

    private User patcher(UserDto userDto, User user) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}
