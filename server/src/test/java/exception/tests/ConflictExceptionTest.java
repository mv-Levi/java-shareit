package exception.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ConflictException;

import static org.assertj.core.api.Assertions.assertThat;

class ConflictExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Conflict occurred";
        ConflictException ex = new ConflictException(message);

        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
