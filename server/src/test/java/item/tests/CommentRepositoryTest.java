package item.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByItemId() {
        User author = new User();
        author.setName("Comment Author");
        author.setEmail("author@example.com");
        author = userRepository.save(author);

        User owner = new User();
        owner.setName("Item Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Comment comment1 = new Comment();
        comment1.setText("First comment");
        comment1.setAuthor(author);
        comment1.setItem(item);
        comment1.setCreated(LocalDateTime.now());
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Second comment");
        comment2.setAuthor(author);
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now().plusMinutes(5));
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByItemId(item.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting("text")
                .containsExactlyInAnyOrder("First comment", "Second comment");
    }
}
