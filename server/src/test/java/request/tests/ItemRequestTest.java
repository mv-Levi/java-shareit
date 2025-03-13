package request.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestTest {

    @Test
    void testEquals_sameObject() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);

        assertThat(request.equals(request)).isTrue();
    }

    @Test
    void testEquals_null() {
        ItemRequest request = new ItemRequest();
        request.setId(2L);

        assertThat(request.equals(null)).isFalse();
    }

    @Test
    void testEquals_differentClass() {
        ItemRequest request = new ItemRequest();
        request.setId(3L);

        String differentType = "I'm a string";
        assertThat(request.equals(differentType)).isFalse();
    }

    @Test
    void testEquals_sameId() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(100L);
        request1.setDescription("Need something");
        request1.setCreated(LocalDateTime.now().minusDays(1));

        ItemRequest request2 = new ItemRequest();
        request2.setId(100L);
        request2.setDescription("Need something else");
        request2.setCreated(LocalDateTime.now());

        assertThat(request1.equals(request2)).isTrue();
        assertThat(request2.equals(request1)).isTrue();
    }

    @Test
    void testEquals_differentId() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(200L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(201L);

        assertThat(request1.equals(request2)).isFalse();
    }

    @Test
    void testHashCode_sameId() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(10L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(10L);

        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testHashCode_differentId() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(11L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(12L);

        assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        ItemRequest request = new ItemRequest();
        request.setId(300L);
        request.setDescription("Need a bike");
        request.setCreated(LocalDateTime.of(2025, 3, 15, 12, 0));
        User user = new User();
        user.setId(50L);
        request.setRequestor(user);

        assertThat(request.getId()).isEqualTo(300L);
        assertThat(request.getDescription()).isEqualTo("Need a bike");
        assertThat(request.getCreated()).isEqualTo("2025-03-15T12:00");
        assertThat(request.getRequestor()).isEqualTo(user);
    }
}
