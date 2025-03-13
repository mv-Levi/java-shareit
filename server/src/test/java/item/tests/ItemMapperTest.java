package item.tests;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    @Test
    void testToItemDtoWhenRequestPresent() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner One");
        owner.setEmail("owner1@example.com");

        ItemRequest request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Need this item");

        Item item = new Item();
        item.setId(100L);
        item.setName("Laptop");
        item.setDescription("High performance laptop");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getName()).isEqualTo("Laptop");
        assertThat(dto.getDescription()).isEqualTo("High performance laptop");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getOwnerId()).isEqualTo(1L);
        assertThat(dto.getIsRequest()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(10L);
    }

    @Test
    void testToItemDtoWhenRequestAbsent() {
        User owner = new User();
        owner.setId(2L);
        owner.setName("Owner Two");
        owner.setEmail("owner2@example.com");

        Item item = new Item();
        item.setId(200L);
        item.setName("Desktop");
        item.setDescription("Office desktop computer");
        item.setAvailable(false);
        item.setOwner(owner);
        item.setRequest(null);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(200L);
        assertThat(dto.getName()).isEqualTo("Desktop");
        assertThat(dto.getDescription()).isEqualTo("Office desktop computer");
        assertThat(dto.getAvailable()).isFalse();
        assertThat(dto.getOwnerId()).isEqualTo(2L);
        assertThat(dto.getIsRequest()).isFalse();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void testToItem() {
        ItemDto dto = new ItemDto();
        dto.setId(300L);
        dto.setName("Mapped Item");
        dto.setDescription("Mapped item description");
        dto.setAvailable(true);
        dto.setRequestId(15L);

        User owner = new User();
        owner.setId(3L);
        owner.setName("Owner Three");
        owner.setEmail("owner3@example.com");

        ItemRequest request = new ItemRequest();
        request.setId(15L);
        request.setDescription("Request for item");

        Item item = ItemMapper.toItem(dto, owner, request);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(300L);
        assertThat(item.getName()).isEqualTo("Mapped Item");
        assertThat(item.getDescription()).isEqualTo("Mapped item description");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(request);
    }
}
