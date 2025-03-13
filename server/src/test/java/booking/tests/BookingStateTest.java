package booking.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingState;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingStateTest {

    @Test
    void testFromString_allValidValues() {
        assertThat(BookingState.fromString("ALL")).isEqualTo(BookingState.ALL);
        assertThat(BookingState.fromString("CURRENT")).isEqualTo(BookingState.CURRENT);
        assertThat(BookingState.fromString("PAST")).isEqualTo(BookingState.PAST);
        assertThat(BookingState.fromString("FUTURE")).isEqualTo(BookingState.FUTURE);
        assertThat(BookingState.fromString("WAITING")).isEqualTo(BookingState.WAITING);
        assertThat(BookingState.fromString("REJECTED")).isEqualTo(BookingState.REJECTED);
    }

    @Test
    void testFromString_caseInsensitive() {
        assertThat(BookingState.fromString("all")).isEqualTo(BookingState.ALL);
        assertThat(BookingState.fromString("cUrrent")).isEqualTo(BookingState.CURRENT);
        assertThat(BookingState.fromString("PaSt")).isEqualTo(BookingState.PAST);
        assertThat(BookingState.fromString("fUtURe")).isEqualTo(BookingState.FUTURE);
        assertThat(BookingState.fromString("wAiTing")).isEqualTo(BookingState.WAITING);
        assertThat(BookingState.fromString("rEjEcTeD")).isEqualTo(BookingState.REJECTED);
    }

    @Test
    void testFromString_unknownState() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> BookingState.fromString("UNKNOWN_STATE")
        );
        assertThat(ex.getMessage()).contains("Unknown booking state: UNKNOWN_STATE");
    }

    @Test
    void testFromString_nullValue() {
        assertThrows(NullPointerException.class, () -> BookingState.fromString(null));
    }
}
