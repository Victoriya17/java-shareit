package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private final String url = "/requests";
    private final String headerUserId = "X-Sharer-User-Id";

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Need a drill");
        itemRequestDto.setRequestorId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setItems(List.of());
    }

    @Test
    void createItemRequestTest() throws Exception {
        NewRequest newRequest = new NewRequest();
        newRequest.setDescription("Need a drill");

        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mvc.perform(post(url)
                        .header(headerUserId, 1L)
                        .content(mapper.writeValueAsString(newRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void findAllByRequestorIdTest() throws Exception {
        when(itemRequestService.findAllItemRequestsByRequestorId(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get(url)
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()));
    }

    @Test
    void findAllOtherUsersRequestsTest() throws Exception {
        when(itemRequestService.findAllItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get(url + "/all")
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findRequestByIdTest() throws Exception {
        when(itemRequestService.findRequestById(anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get(url + "/" + itemRequestDto.getId())
                        .header(headerUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.items").isArray());
    }
}
