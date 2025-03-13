package exception.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;

class BadRequestExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Bad request happened";
        BadRequestException ex = new BadRequestException(message);

        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
