package ru.yandex.practicum.shareIt.request;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.exeptions.ValidationException;
import ru.yandex.practicum.shareIt.request.model.dto.IncomingRequestDto;

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
                        .content(mapper.writeValueAsString(new IncomingRequestDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("error"));
    }
}