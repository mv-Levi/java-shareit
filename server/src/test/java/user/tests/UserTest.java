package user.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testEquals_sameObject() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        assertThat(user.equals(user)).isTrue();
    }

    @Test
    void testEquals_null() {
        User user = new User();
        user.setId(2L);

        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void testEquals_differentClass() {
        User user = new User();
        user.setId(3L);

        String other = "some string";
        assertThat(user.equals(other)).isFalse();
    }

    @Test
    void testEquals_sameId() {
        User user1 = new User();
        user1.setId(10L);

        User user2 = new User();
        user2.setId(10L);

        assertThat(user1.equals(user2)).isTrue();
        assertThat(user2.equals(user1)).isTrue();
    }

    @Test
    void testEquals_differentId() {
        User user1 = new User();
        user1.setId(11L);

        User user2 = new User();
        user2.setId(12L);

        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    void testHashCode_sameId() {
        User user1 = new User();
        user1.setId(20L);

        User user2 = new User();
        user2.setId(20L);

        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testHashCode_differentId() {
        User user1 = new User();
        user1.setId(21L);

        User user2 = new User();
        user2.setId(22L);

        assertThat(user1.hashCode()).isNotEqualTo(user2.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(100L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        assertThat(user.getId()).isEqualTo(100L);
        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
    }
}
