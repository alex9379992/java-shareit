package ru.yandex.practicum.shareIt.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.shareIt.BaseTest;

import ru.yandex.practicum.shareIt.exeptions.UserNotFoundException;
import ru.yandex.practicum.shareIt.mapper.Mapper;
import ru.yandex.practicum.shareIt.user.model.*;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest extends BaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private Mapper mapper;
    @InjectMocks
    UserServiceImpl userService;



    @Test
    void createUserTest_WhenUserValid_ThenReturnUserDto() {
        UserDto userDto = createUserDto(1L, "alex", "alex@email.com");

        User user = new User();
        user.setName("alex");
        user.setEmail("alex@email.com");
        when(mapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toUserDto(user)).thenReturn(userDto);
        UserDto afterSavedUserDto = userService.createUser(userDto);

        assertEquals(userDto.getName(), afterSavedUserDto.getName());
        verify(userRepository).save(user);
    }



    @Test
    void patchUserTest_whenUpdateFields_thanUpdateFields() {
        long userId = 1L;
        UserDto userDto = createUserDto(1L, "newName", "newEmail@mail.com");//UserDto.builder().name("newName").email("newEmail@mail.com").build();

        User user = createUser(userId, "alex", "alex@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
       when(mapper.toUserDto(user)).thenReturn(createUserDto(1L, "newName", "newEmail@mail.com"));

        UserDto afterUpdateUserDto = userService.patchUser(userDto, userId);

        assertEquals(userDto.getName(), afterUpdateUserDto.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
        verify(userRepository).save(user);

    }

    @Test
    void patchUserTest_whenUpdateFields_thanNotUserFound() {
        long userId = 2L;
        UserDto userDto = createUserDto(2L, "newName", "newEmail@mail.com");
        User user = createUser(1L, "alex", "alex@mail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.patchUser(userDto, userId));
        verify(userRepository, never()).save(user);
    }

    @Test
    void getUserDtoTest_whenFindUser_thenFindUser() {
        long userId = 1L;
       User user = createUser(1L, "alex", "alex@mail.com");


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toUserDto(user)).thenReturn(createUserDto(1L, "alex", "alex@mail.com"));

        UserDto userDto = userService.getUserDto(userId);
        assertEquals(user.getId(), userDto.getId(), "айдишники должны совпадать");
    }

    @Test
    void getUserDtoTest_whenFindUser_thenNotFindUser() {
        long userId = 1L;
        User user = createUser(2L, "alex", "alex@mail.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.getUserDto(userId));

        verify(mapper, never()).toUserDto(user);
    }

    @Test
    void deleteUserTest_whenDeleteUser_thenDeleteUser() {
        long userId = 1L;
        User user = createUser(1L, "alex", "alex@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserTest_whenDeleteUser_thenNotDeleteUser() {
        long userId = 2L;
        User user = createUser(1L, "alex", "alex@mail.com");

        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(userId));

        verify(userRepository, never()).delete(user);
    }

    @Test
    void getUsersTest_whenGetUsers() {
        User user1 =createUser(1L, "alex", "alex@mail.com");
        User user2 = createUser(2L, "mark", "mark@mail.com");
        UserDto userDto1 = createUserDto(1L, "alex", "alex@mail.com");
        UserDto userDto2 = createUserDto(2L, "mark", "mark@mail.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(mapper.mapToUserDtoList(List.of(user1, user2))).thenReturn(List.of(userDto1, userDto2));

        List<UserDto> userDtoList = userService.getUsers();

        assertEquals(userDtoList.size(), 2);
        assertEquals(userDtoList.get(0).getName(), "alex");
    }

    @Test
    void findUserByIdTest_WhenFindUser_ThenFindUser() {
        User user =createUser(1L, "alex", "alex@mail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User findUser = userService.findUserById(1L);

        verify(userRepository).findById(1L);
        assertEquals(user.getName(), findUser.getName());
    }

    @Test
    void findUserByIdTest_WhenFindUser_ThenNotFindUser() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.findUserById(1L));
    }

    @Test
    void patcherTest_WhenPatchField_ThenPatchFields() {
        User user =createUser(1L, "alex", "alex@mail.com");
        UserDto userDto = createUserDto(2L, "mark", "mark@mail.com");

        User afterPatchUser = userService.patcher(userDto, user);

        assertEquals(userDto.getName(), afterPatchUser.getName());
        assertEquals(userDto.getEmail(), afterPatchUser.getEmail());
    }
}