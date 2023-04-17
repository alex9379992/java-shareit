package ru.yandex.practicum.shareIt.request;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.yandex.practicum.shareIt.request.model.IncomingRequest;
import ru.yandex.practicum.shareIt.request.model.RequestDto;
import ru.yandex.practicum.shareIt.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({RequestController.class})
class RequestControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestServiceImpl service;

    @Test
    void createRequest() throws Exception {
        when(service.createRequest(any(), anyLong())).
                thenReturn(createRequestDto(2L,
                        createUser(1L, "name", "name@mail.com"),
                        "Хотел бы воспользоваться мясорубкой"));
        mockMvc.perform(post("/requests")
                        .header(xShareUserId, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createIncomingRequest("Хотел бы воспользоваться мясорубкой"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться мясорубкой"));
    }

    @Test
    void createUserNotFound() throws Exception {
        when(service.createRequest(any(), anyLong())).thenThrow(new SearchException("User not found"));

        mockMvc.perform(post("/requests")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createIncomingRequest("Хотел бы воспользоваться мясорубкой"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void createValidationError() throws Exception {
        when(service.createRequest(any(), anyLong())).thenThrow(new ValidationException("Description is null"));

        mockMvc.perform(post("/requests")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new IncomingRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void findRequestDtoListFromUser() throws Exception {
        User user = createUser(2L, "name", "name@mail.com");
        RequestDto requestDto1 = createRequestDto(2L, user, "Хотел бы воспользоваться мясорубкой");
        RequestDto requestDto2 = createRequestDto(4L, user, "Хотел бы воспользоваться бензопилой");
        requestDto1.setItems(List.of(createResponseDto(11L, "Бензопила", "Бензопила \"Дружба\" новая, в упаковке",
                true, 4L, 2L, LocalDateTime.now())));
        when(service.findAllRequestDtoListFromPagination(anyLong(), anyLong(), anyLong())).thenReturn(List.of(requestDto1, requestDto2));
        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header(xShareUserId, 2)
                        .param("from", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].requester").value(2))
                .andExpect(jsonPath("$[0].description").value("Хотел бы воспользоваться мясорубкой"))
                .andExpect(jsonPath("$[0].items").isEmpty())
                .andExpect(jsonPath("$[1].requester").value(2))
                .andExpect(jsonPath("$[1].items[0].id").value(11))
                .andExpect(jsonPath("$[1].items[0].name").value("Бензопила"))
                .andExpect(jsonPath("$[1].items[0].description").value("Бензопила \"Дружба\" новая, в упаковке"))
                .andExpect(jsonPath("$[1].description").value("Хотел бы воспользоваться бензопилой"));
    }

    @Test
    void findRequestDtoFromId() {
    }

    @Test
    void findAllRequestDtoList() {
    }
}