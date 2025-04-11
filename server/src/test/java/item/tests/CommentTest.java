package item.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    @Test
    void testEqualsAndHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("First comment");

        Comment comment2 = new Comment();
        comment2.setId(1L);
        comment2.setText("Second comment");

        Comment comment3 = new Comment();
        comment3.setId(2L);
        comment3.setText("Third comment");

        assertThat(comment1.equals(comment1)).isTrue();
        assertThat(comment1.equals(null)).isFalse();
        assertThat(comment1.equals("some string")).isFalse();
        assertThat(comment1.equals(comment2)).isTrue();
        assertThat(comment1.equals(comment3)).isFalse();

        assertThat(comment1.hashCode()).isEqualTo(comment2.hashCode());
        assertThat(comment1.hashCode()).isNotEqualTo(comment3.hashCode());
    }
}