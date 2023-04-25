package ru.yandex.practicum.shareIt.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.exeptions.ItemNotFoundException;
import ru.yandex.practicum.shareIt.exeptions.UserNotFoundException;
import ru.yandex.practicum.shareIt.item.model.IncomingItem;
import ru.yandex.practicum.shareIt.item.model.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({ItemController.class})
class ItemControllerTest extends BaseTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper  mapper;
    @MockBean
    private ItemServiceImpl service;

    @Test
    void createItem() throws Exception {
        IncomingItem incomingItem = createIncomingItem();
        when(service.createItem(11L, incomingItem)).thenReturn(buildItemDto(12L, "Бензопила", "Бензопила \"Дружба\"", true));

        mockMvc.perform(post("/items")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(incomingItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Бензопила"))
                .andExpect(jsonPath("$.description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void patchItem() throws Exception {
        when(service.patchItem( any(), anyLong(),anyLong())).
                thenReturn(buildItemDto(12L, "Бензопила", "Бензопила \"Дружба\"", true));

        mockMvc.perform(patch("/items/12")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Бензопила"))
                .andExpect(jsonPath("$.description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void patchItemUserNotFound() throws Exception {
        when(service.patchItem( any(), anyLong(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(patch("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void patchItemItemNotFound() throws Exception {
        when(service.patchItem( any(), anyLong(),anyLong())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(patch("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/12")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        when(service.getItemDto(anyLong(), anyLong())).thenReturn(buildItemDto(12L, "Бензопила", "Бензопила \"Дружба\"", true));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/12")
                        .header(xShareUserId, 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.name").value("Бензопила"))
                .andExpect(jsonPath("$.description").value("Бензопила \"Дружба\""))
                .andExpect(jsonPath("$.lastBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.nextBooking").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getByIdItemNotFound() throws Exception {
        when(service.getItemDto(anyLong(), anyLong())).thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"));
    }

    @Test
    void getByIdUserNotFound() throws Exception {
        when(service.getItemDto(anyLong(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/15")
                        .header(xShareUserId, 18L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("error"));
    }
}