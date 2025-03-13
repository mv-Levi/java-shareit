package item.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByOwnerId() {
        // Arrange: создаем пользователя и две вещи, принадлежащие ему
        User owner = new User();
        owner.setName("Owner Test");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        Item item1 = new Item();
        item1.setName("Item One");
        item1.setDescription("Description One");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item Two");
        item2.setDescription("Description Two");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        // Act: получаем вещи по ownerId
        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        // Assert: проверяем, что найдено 2 вещи и их имена соответствуют ожиданиям
        assertThat(items).hasSize(2);
        assertThat(items).extracting("name")
                .containsExactlyInAnyOrder("Item One", "Item Two");
    }

    @Test
    void testSearchItemsByText_OnlyAvailableItems() {
        // Arrange: создаем пользователя (владельца)
        User owner = new User();
        owner.setName("Owner Test");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        Item item1 = new Item();
        item1.setName("Laptop");
        item1.setDescription("High performance laptop");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Desktop");
        item2.setDescription("Office desktop computer");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Chair");
        item3.setDescription("Ergonomic office chair");
        item3.setAvailable(false);
        item3.setOwner(owner);
        itemRepository.save(item3);

        List<Item> result = itemRepository
                .findByAvailableAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(true, "office", "office");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Desktop");
    }
}
