package item.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemTest {

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item One");

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Item Two");

        Item item3 = new Item();
        item3.setId(2L);
        item3.setName("Item Three");

        assertThat(item1.equals(item1)).isTrue();
        assertThat(item1.equals(null)).isFalse();
        assertThat(item1.equals("some string")).isFalse();
        assertThat(item1.equals(item2)).isTrue();
        assertThat(item1.equals(item3)).isFalse();

        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
        assertThat(item1.hashCode()).isNotEqualTo(item3.hashCode());
    }
}