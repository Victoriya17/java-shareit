package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private final String url = "/bookings";
    private final String headerUserId = "X-Sharer-User-Id";

    private BookingDto bookingDto;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(
                        new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(mapper))
                .build();

        userDto = new UserDto(1L, "John Doe", "john.doe@mail.com");
        itemDto = new ItemDto(1L, "name", "description", 1L, Boolean.TRUE, 1L);
        bookingDto = new BookingDto(1L, LocalDateTime.of(2025, 5, 3, 18, 15, 12),
                LocalDateTime.of(2025, 5, 5, 17, 20, 15), itemDto, Status.APPROVED, userDto);
    }

    @Test
    void createTest() throws Exception {
        Map<String, Object> newBookingRequest = Map.of(
                "start", "2025-05-03T18:15:12",
                "end", "2025-05-05T17:20:15",
                "itemId", itemDto.getId()
        );

        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);

        mvc.perform(post(url)
                        .header(headerUserId, 1L)
                        .content(mapper.writeValueAsString(newBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item.id").value(itemDto.getId()));
    }

    @Test
    void approveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch(url + "/" + bookingDto.getId())
                        .header(headerUserId, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void findBookingTest() throws Exception {
        when(bookingService.findBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get(url + "/" + bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)))
                .andExpect(jsonPath("$.item").exists())
                .andExpect(jsonPath("$.item.id").value(is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name").value(is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.item.description").value(is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.booker").exists())
                .andExpect(jsonPath("$.booker.id").value(is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name").value(is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.booker.email").value(is(userDto.getEmail()), String.class));
    }

    @Test
    void findAllBookingsByUserTest() throws Exception {
        BookingDto bookingDto2 = new BookingDto(2L, LocalDateTime.of(2025, 5, 3, 18, 15, 12),
                LocalDateTime.of(2025, 5, 5, 17, 20, 15), itemDto, Status.REJECTED, userDto);
        List<BookingDto> newRequests = List.of(bookingDto, bookingDto2);

        when(bookingService.findAllBookingsByUser(anyLong(), anyString()))
                .thenReturn(newRequests);

        mvc.perform(get(url)
                        .header(headerUserId, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
                .andExpect(content().json(mapper.writeValueAsString(newRequests)));
    }

    @Test
    void findAllBookingsByOwnerItemsTest() throws Exception {
        BookingDto bookingDto2 = new BookingDto(2L, LocalDateTime.of(2025, 5, 3, 18, 15, 12),
                LocalDateTime.of(2025, 5, 5, 17, 20, 15), itemDto, Status.REJECTED, userDto);
        List<BookingDto> newRequests = List.of(bookingDto, bookingDto2);

        when(bookingService.findAllBookingsByOwnerItems(anyLong(), anyString()))
                .thenReturn(newRequests);

        mvc.perform(get(url + "/owner")
                        .header(headerUserId, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
                .andExpect(content().json(mapper.writeValueAsString(newRequests)));
    }
}
