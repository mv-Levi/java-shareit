package exception.tests;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exceptions.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleBadRequest() {
        String message = "Test bad request error";
        BadRequestException ex = new BadRequestException(message);
        ResponseEntity<Map<String, String>> response = handler.handleBadRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("400");
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).isEqualTo(message);
    }

    @Test
    void testHandleIllegalArgumentForNotNullMessage() {
        String errorMsg = "Parameter must not be null";
        IllegalArgumentException ex = new IllegalArgumentException(errorMsg);
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(ex);

        String message = "Value must not be null";
        ex = new IllegalArgumentException(message);
        response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("404");
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body.get("message")).isEqualTo("Item not found with ID: null");
    }

    @Test
    void testHandleIllegalArgumentForOtherMessage() {
        String message = "Invalid parameter";
        IllegalArgumentException ex = new IllegalArgumentException(message);
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("400");
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).isEqualTo(message);
    }

    @Test
    void testHandleConflict() {
        String message = "Conflict occurred";
        ConflictException ex = new ConflictException(message);
        ResponseEntity<Map<String, String>> response = handler.handleConflict(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("409");
        assertThat(body.get("error")).isEqualTo("Conflict");
        assertThat(body.get("message")).isEqualTo(message);
    }

    @Test
    void testHandleNotFound() {
        String message = "Entity not found";
        NotFoundException ex = new NotFoundException(message);
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("404");
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body.get("message")).isEqualTo(message);
    }

    @Test
    void testHandleForbidden() {
        String message = "Access denied";
        ForbiddenException ex = new ForbiddenException(message);
        ResponseEntity<Map<String, String>> response = handler.handleForbidden(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("403");
        assertThat(body.get("error")).isEqualTo("Forbidden");
        assertThat(body.get("message")).isEqualTo(message);
    }

    @Test
    void testHandleAllExceptionsWhenCauseIsNotFound() {
        NotFoundException notFoundEx = new NotFoundException("Not found error");
        Exception wrapper = new Exception("Wrapper exception", notFoundEx);
        ResponseEntity<Map<String, String>> response = handler.handleAllExceptions(wrapper);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("404");
        assertThat(body.get("error")).isEqualTo("Not Found");
        assertThat(body.get("message")).isEqualTo("Not found error");
    }

    @Test
    void testHandleAllExceptionsDefault() {
        Exception ex = new Exception("Some other error");
        ResponseEntity<Map<String, String>> response = handler.handleAllExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("500");
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("An unexpected error occurred.");
    }

    @Test
    void testHandleNullPointer() {
        NullPointerException ex = new NullPointerException("Null pointer exception");
        ResponseEntity<Map<String, String>> response = handler.handleNullPointer(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("500");
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("A null pointer exception occurred.");
    }
}
