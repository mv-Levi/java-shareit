package user.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmailWhenUserExists() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("john@example.com");

        assertThat(found).isPresent();
        User foundUser = found.get();
        assertThat(foundUser.getName()).isEqualTo("John Doe");
        assertThat(foundUser.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testFindByEmailWhenUserDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isNotPresent();
    }
}
