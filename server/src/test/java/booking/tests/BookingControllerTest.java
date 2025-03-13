package booking.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {ShareItServer.class})
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddBookingSuccess() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2025, 3, 15, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 3, 16, 10, 0));
        bookingDto.setStatus(Status.WAITING);
        Long userId = 42L;

        Mockito.when(bookingService.createBooking(Mockito.eq(userId), Mockito.any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value("2025-03-15T10:00:00"))
                .andExpect(jsonPath("$.end").value("2025-03-16T10:00:00"));
    }

    @Test
    void testUpdateBookingStatus() throws Exception {
        // Arrange
        Long bookingId = 1L;
        Long userId = 42L;
        boolean approved = true;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(LocalDateTime.of(2025, 3, 15, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 3, 16, 10, 0));
        bookingDto.setStatus(Status.APPROVED);
        // Мокаем сервис
        Mockito.when(bookingService.updateBookingStatus(Mockito.eq(bookingId), Mockito.eq(approved), Mockito.eq(userId)))
                .thenReturn(bookingDto);

        // Act & Assert
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.start").value("2025-03-15T10:00:00"))
                .andExpect(jsonPath("$.end").value("2025-03-16T10:00:00"));
    }

    @Test
    void testGetBooking() throws Exception {
        Long bookingId = 2L;
        Long userId = 200L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(LocalDateTime.of(2025, 4, 10, 12, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 4, 11, 12, 0));
        bookingDto.setStatus(Status.WAITING);

        Mockito.when(bookingService.getBookingById(bookingId, userId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value("2025-04-10T12:00:00"))
                .andExpect(jsonPath("$.end").value("2025-04-11T12:00:00"));
    }

    @Test
    void testGetUserBookings() throws Exception {
        Long userId = 300L;
        BookingDto booking1 = new BookingDto();
        booking1.setId(3L);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.of(2025, 5, 1, 8, 0));
        booking1.setEnd(LocalDateTime.of(2025, 5, 1, 10, 0));
        BookingDto booking2 = new BookingDto();
        booking2.setId(4L);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.of(2025, 5, 2, 8, 0));
        booking2.setEnd(LocalDateTime.of(2025, 5, 2, 10, 0));
        // Предполагаем, что сервис возвращает список, отсортированный по start DESC (например, booking2, затем booking1)
        List<BookingDto> bookingList = List.of(booking2, booking1);
        Mockito.when(bookingService.getBookingsByUser(userId, "ALL")).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[1].id").value(3));
    }

    @Test
    void testGetOwnerBookings() throws Exception {
        // Arrange
        Long userId = 400L;
        BookingDto booking1 = new BookingDto();
        booking1.setId(5L);
        booking1.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.of(2025, 6, 1, 9, 0));
        booking1.setEnd(LocalDateTime.of(2025, 6, 1, 11, 0));
        BookingDto booking2 = new BookingDto();
        booking2.setId(6L);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.of(2025, 6, 2, 9, 0));
        booking2.setEnd(LocalDateTime.of(2025, 6, 2, 11, 0));
        // Список отсортирован по start DESC (booking2, потом booking1)
        List<BookingDto> bookingList = List.of(booking2, booking1);
        Mockito.when(bookingService.getBookingsByOwner(userId, "ALL")).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(6))
                .andExpect(jsonPath("$[1].id").value(5));
    }
}
