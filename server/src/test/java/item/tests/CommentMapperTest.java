package item.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toDtoTest() {
        User author = new User();
        author.setId(1L);
        author.setName("Test Author");
        author.setEmail("author@test.com");

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("This is a test comment.");
        comment.setAuthor(author);
        LocalDateTime created = LocalDateTime.of(2025, 3, 20, 10, 30);
        comment.setCreated(created);

        CommentDto dto = CommentMapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getText()).isEqualTo("This is a test comment.");
        assertThat(dto.getAuthorName()).isEqualTo("Test Author");
        assertThat(dto.getCreated()).isEqualTo(created);
    }
}
