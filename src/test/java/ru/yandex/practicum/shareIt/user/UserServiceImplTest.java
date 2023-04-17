package ru.yandex.practicum.shareIt.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.exeptions.ValidationException;
import ru.yandex.practicum.shareIt.user.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest extends BaseTest {

//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private UserMapper userMapper;
//    @Mock
//    private UserValidator userValidator;
//    @InjectMocks
//    UserServiceImpl userService;
//
//    @Test
//    void createUserTest_WhenUserValid_ThenSavedUser() throws Exception {
//        UserDto userDto = createUserDto(1L, "alex", "alex@email.com");//UserDto.builder().name("alex").email().build();
//        when(userValidator.validation(userDto)).thenReturn(true);
//        User user = new User();
//        user.setName("alex");
//        user.setEmail("alex@email.com");
//
//        when(userMapper.toUser(userDto)).thenReturn(user);
//        when(userMapper.toUserDto(user)).thenReturn(createUserDto(1L, "alex", "alex@email.com"));
//        when(userRepository.save(user)).thenReturn(user);
//
//        UserDto afterSavedUserDto = userService.createUser(userDto);
//
//        assertEquals(userDto.getName(), afterSavedUserDto.getName());
//        verify(userRepository).save(user);
//    }
//
//    @Test
//    void createUserTest_WhenUserValid_ThenNotSavedUser() {
//        UserDto userDto = createUserDto(1L, "alex", "");
//
//        User user = new User();
//        user.setName("alex");
//        user.setEmail("alex@email.com");
//
//        when(userValidator.validation(userDto)).thenReturn(false);
//
//        assertThrows(ValidationException.class, () ->
//                userService.createUser(userDto));
//        verify(userRepository, never()).save(user);
//    }
//
//    @Test
//    void patchUserTest_whenUpdateFields_thanUpdateFields() {
//        long userId = 1L;
//        UserDto userDto = createUserDto(1L, "newName", "newEmail@mail.com");//UserDto.builder().name("newName").email("newEmail@mail.com").build();
//
//        User user = new User();
//        user.setId(1L);
//        user.setName("alex");
//        user.setEmail("alex@mail.com");
//
//        when(userRepository.findAll()).thenReturn(List.of(user));
//        when(userMapper.mapToUsersMap(List.of(user))).thenReturn(Map.of(1L, user));
//        when(userValidator.isThereAUser(userId, Map.of(1L, user))).thenReturn(true);
//        when(userRepository.getById(userId)).thenReturn(user);
//        when(userMapper.toUserDto(user)).thenReturn(createUserDto(1L, "newName", "newEmail@mail.com"));
//
//        UserDto afterUpdateUserDto = userService.patchUser(userDto, userId);
//        assertEquals(userDto.getName(), afterUpdateUserDto.getName());
//        verify(userRepository).save(user);
//
//    }
//
//    @Test
//    void patchUserTest_whenUpdateFields_thanNotUpdateFields() {
//        long userId = 2L;
//        UserDto userDto = createUserDto(1L, "newName", "newEmail@mail.com");
//        User user = new User();
//        user.setId(1L);
//        user.setName("alex");
//        user.setEmail("alexl@mail.com");
//
//        when(userRepository.findAll()).thenReturn(List.of(user));
//        when(userMapper.mapToUsersMap(List.of(user))).thenReturn(Map.of(1L, user));
//        when(userValidator.isThereAUser(userId, Map.of(1L, user))).thenReturn(false);
//
//        assertThrows(SearchException.class, () ->
//                userService.patchUser(userDto, userId));
//        verify(userRepository, never()).save(user);
//    }
//
//    @Test
//    void getUserDtoTest_whenFindUser_thenFindUser() {
//        long userId = 1L;
//        User user = new User();
//        user.setId(1L);
//        user.setName("alex");
//        user.setEmail("alexl@mail.com");
//
//        when(userRepository.findAll()).thenReturn(List.of(user));
//        when(userMapper.mapToUsersMap(List.of(user))).thenReturn(Map.of(1L, user));
//        when(userValidator.isThereAUser(userId, Map.of(1L, user))).thenReturn(true);
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userMapper.toUserDto(user)).thenReturn(createUserDto(1L, "alex", "alex@email.com"));
//
//        UserDto userDto = userService.getUserDto(userId);
//        assertEquals(user.getId(), userDto.getId(), "айдишники должны совпадать");
//    }
//
//    @Test
//    void getUserDtoTest_whenFindUser_thenNotFindUser() {
//        long userId = 1L;
//        User user = new User();
//        user.setId(2L);
//        user.setName("alex");
//        user.setEmail("alexl@mail.com");
//
//        when(userRepository.findAll()).thenReturn(List.of(user));
//        when(userMapper.mapToUsersMap(List.of(user))).thenReturn(Map.of(2L, user));
//        when(userValidator.isThereAUser(userId, Map.of(2L, user))).thenReturn(false);
//
//        assertThrows(SearchException.class, () ->
//                userService.getUserDto(userId));
//        verify(userRepository, never()).findById(userId);
//    }
//
//    @Test
//    void deleteUserTest_whenDeleteUser_thenDeleteUser() {
//        long userId = 1L;
//        User user = new User();
//        user.setId(1L);
//        user.setName("alex");
//        user.setEmail("alexl@mail.com");
//
//        when(userRepository.findAll()).thenReturn(List.of(user));
//        when(userMapper.mapToUsersMap(List.of(user))).thenReturn(Map.of(1L, user));
//        when(userValidator.isThereAUser(userId, Map.of(1L, user))).thenReturn(true);
//
//        userService.deleteUser(userId);
//        verify(userRepository).deleteById(userId);
//    }
//
//    @Test
//    void deleteUserTest_whenDeleteUser_thenNotDeleteUser() {
//        long userId = 2L;
//        User user = new User();
//        user.setId(1L);
//        user.setName("alex");
//        user.setEmail("alexl@mail.com");
//
//        when(userRepository.findAll()).thenReturn(List.of(user));
//        when(userMapper.mapToUsersMap(List.of(user))).thenReturn(Map.of(2L, user));
//        when(userValidator.isThereAUser(userId, Map.of(2L, user))).thenReturn(false);
//
//        assertThrows(SearchException.class, () ->
//                userService.deleteUser(userId));
//        verify(userRepository, never()).deleteById(userId);
//    }
//
//    @Test
//    void getUsersTest_whenGetUsers() {
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setName("alex");
//        user1.setEmail("alexl@mail.com");
//
//        User user2 = new User();
//        user2.setId(2L);
//        user2.setName("mark");
//        user2.setEmail("mark@mail.com");
//
//        UserMapper userMapper2 = new UserMapperImpl();
//        UserDto userDto = userMapper2.toUserDto(user1);
//
//        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
//        when(userMapper.mapToUserDtoList(List.of(user1, user2))).thenReturn(userMapper2.mapToUserDtoList(List.of(user1, user2)));
//
//        List<UserDto> userDtoList = userService.getUsers();
//
//        assertEquals(2, userDtoList.size());
//        assertEquals(userDto.getName(), userDtoList.get(0).getName());
//    }
}