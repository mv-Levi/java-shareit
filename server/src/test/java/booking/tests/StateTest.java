package booking.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.State;

import static org.assertj.core.api.Assertions.assertThat;

public class StateTest {

    @Test
    void testEnumValues() {
        State[] values = State.values();
        assertThat(values)
                .hasSize(6)
                .containsExactly(State.ALL, State.CURRENT, State.PAST, State.FUTURE, State.WAITING, State.REJECTED);
    }

    @Test
    void testValueOf() {
        assertThat(State.valueOf("ALL")).isEqualTo(State.ALL);
        assertThat(State.valueOf("CURRENT")).isEqualTo(State.CURRENT);
        assertThat(State.valueOf("PAST")).isEqualTo(State.PAST);
        assertThat(State.valueOf("FUTURE")).isEqualTo(State.FUTURE);
        assertThat(State.valueOf("WAITING")).isEqualTo(State.WAITING);
        assertThat(State.valueOf("REJECTED")).isEqualTo(State.REJECTED);
    }
}
