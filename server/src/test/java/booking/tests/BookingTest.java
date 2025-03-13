package booking.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void testEquals_sameObject() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking.equals(booking)).isTrue();
    }

    @Test
    void testEquals_null() {
        Booking booking = new Booking();
        booking.setId(2L);

        assertThat(booking.equals(null)).isFalse();
    }

    @Test
    void testEquals_differentClass() {
        Booking booking = new Booking();
        booking.setId(3L);

        String otherType = "some string";
        assertThat(booking.equals(otherType)).isFalse();
    }

    @Test
    void testEquals_sameId() {
        Booking booking1 = new Booking();
        booking1.setId(10L);

        Booking booking2 = new Booking();
        booking2.setId(10L);

        assertThat(booking1.equals(booking2)).isTrue();
        assertThat(booking2.equals(booking1)).isTrue();
    }

    @Test
    void testEquals_differentId() {
        Booking booking1 = new Booking();
        booking1.setId(11L);

        Booking booking2 = new Booking();
        booking2.setId(12L);

        assertThat(booking1.equals(booking2)).isFalse();
    }

    @Test
    void testHashCode_sameId() {
        Booking booking1 = new Booking();
        booking1.setId(20L);

        Booking booking2 = new Booking();
        booking2.setId(20L);

        assertThat(booking1.hashCode()).isEqualTo(booking2.hashCode());
    }

    @Test
    void testHashCode_differentId() {
        Booking booking1 = new Booking();
        booking1.setId(21L);

        Booking booking2 = new Booking();
        booking2.setId(22L);

        assertThat(booking1.hashCode()).isNotEqualTo(booking2.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        Booking booking = new Booking();
        booking.setId(100L);

        User user = new User();
        user.setId(1L);
        user.setName("Booker Name");
        booking.setBooker(user);

        Item item = new Item();
        item.setId(2L);
        item.setName("Test Item");
        booking.setItem(item);

        LocalDateTime startTime = LocalDateTime.of(2025, 3, 15, 12, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 3, 16, 12, 0);
        booking.setStart(startTime);
        booking.setEnd(endTime);

        booking.setStatus(Status.WAITING);

        assertThat(booking.getId()).isEqualTo(100L);
        assertThat(booking.getBooker()).isEqualTo(user);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getStart()).isEqualTo(startTime);
        assertThat(booking.getEnd()).isEqualTo(endTime);
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }
}
