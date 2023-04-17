package ru.yandex.practicum.shareIt.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.exeptions.ValidationException;
import ru.yandex.practicum.shareIt.user.model.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({UserController.class})
class UserControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserServiceImpl service;

    @Test
    void createUserTest() throws Exception {
        when(service.createUser(any())).thenReturn(createUserDto(3L, "user","user@mail.com"));

        mockMvc.perform(post("/users")
                        .header(xShareUserId, 38)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserDto(3L,  "user","user@mail.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void createValidationError() throws Exception {
        when(service.createUser(any())).thenThrow(new ValidationException("error"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void patchUser() throws Exception {
         when(service.patchUser( any(),anyLong())).thenReturn(createUserDto(3L,  "user","user@mail.com"));

        mockMvc.perform(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void updateUserNotFound() throws Exception {
        when(service.patchUser(any(),anyLong())).thenThrow(new SearchException("User not found"));

        mockMvc.perform(patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void getUser() throws Exception {
        when(service.getUserDto(anyLong())).thenReturn(createUserDto(3L,  "user","user@mail.com"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("user@mail.com"))
                .andExpect(jsonPath("$.name").value("user"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(service.getUserDto(anyLong())).thenThrow(new SearchException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUsers() throws Exception {
        when(service.getUsers()).thenReturn(List.of(
                createUserDto(1L,"user", "user@mail.com" ),
                createUserDto(2L,"user_user", "user_user@mail.com" )));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user@mail.com"))
                .andExpect(jsonPath("$[0].name").value("user"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("user_user@mail.com"))
                .andExpect(jsonPath("$[1].name").value("user_user"));
    }
}