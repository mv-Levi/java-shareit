package booking.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBookingDtoTest() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2025, 3, 15, 10, 0));
        booking.setEnd(LocalDateTime.of(2025, 3, 16, 10, 0));
        booking.setStatus(Status.WAITING);

        Item item = new Item();
        item.setId(2L);
        item.setName("Item name");
        booking.setItem(item);

        User user = new User();
        user.setId(3L);
        user.setName("User name");
        booking.setBooker(user);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getStatus()).isEqualTo(Status.WAITING);

        assertThat(dto.getItem().getId()).isEqualTo(2L);
        assertThat(dto.getItem().getName()).isEqualTo("Item name");
        assertThat(dto.getBooker().getId()).isEqualTo(3L);
        assertThat(dto.getBooker().getName()).isEqualTo("User name");
    }

    @Test
    void toBookingTest() {
        BookingDto dto = new BookingDto();
        dto.setId(10L);
        dto.setStart(LocalDateTime.of(2025, 4, 1, 12, 0));
        dto.setEnd(LocalDateTime.of(2025, 4, 2, 12, 0));
        dto.setStatus(Status.APPROVED);

        Item item = new Item();
        item.setId(20L);
        User user = new User();
        user.setId(30L);

        Booking booking = BookingMapper.toBooking(dto, item, user);

        assertThat(booking.getId()).isEqualTo(10L);
        assertThat(booking.getStart()).isEqualTo(dto.getStart());
        assertThat(booking.getEnd()).isEqualTo(dto.getEnd());
        assertThat(booking.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(user);
    }

    @Test
    void toEntityTest() {
        BookingDto dto = new BookingDto();
        dto.setId(100L); // toEntity не устанавливает id, так что ожидаем null в результате
        dto.setStart(LocalDateTime.of(2025, 5, 1, 15, 0));
        dto.setEnd(LocalDateTime.of(2025, 5, 2, 15, 0));
        dto.setStatus(Status.REJECTED); // при toEntity игнорируется и ставится WAITING

        Item item = new Item();
        item.setId(200L);
        User user = new User();
        user.setId(300L);

        Booking booking = BookingMapper.toEntity(dto, item, user);

        assertThat(booking.getId()).isNull();
        assertThat(booking.getStart()).isEqualTo(dto.getStart());
        assertThat(booking.getEnd()).isEqualTo(dto.getEnd());
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(user);
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }
}