package item.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void testAddItemUserNotFound() {
        Long nonExistentUserId = 999L;
        ItemDto dto = new ItemDto();
        dto.setName("Item Name");
        dto.setDescription("Item Description");
        dto.setAvailable(true);

        assertThrows(NotFoundException.class, () ->
                itemService.addItem(nonExistentUserId, dto)
        );
    }

    @Test
    void testAddItemRequestNotFound() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        Long nonExistentRequestId = 888L;
        ItemDto dto = new ItemDto();
        dto.setName("Item Name");
        dto.setDescription("Item Description");
        dto.setAvailable(true);
        dto.setRequestId(nonExistentRequestId);

        User finalOwner = owner;
        assertThrows(NotFoundException.class, () ->
                itemService.addItem(finalOwner.getId(), dto)
        );
    }

    @Test
    void testAddItemNameIsNull() {
        User owner = new User();
        owner.setName("Owner2");
        owner.setEmail("owner2@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName(null);
        dto.setDescription("Desc");
        dto.setAvailable(true);

        User finalOwner = owner;
        assertThrows(BadRequestException.class, () ->
                itemService.addItem(finalOwner.getId(), dto)
        );
    }

    @Test
    void testAddItemNameIsEmpty() {
        User owner = new User();
        owner.setName("Owner3");
        owner.setEmail("owner3@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName("");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        User finalOwner = owner;
        assertThrows(BadRequestException.class, () ->
                itemService.addItem(finalOwner.getId(), dto)
        );
    }

    @Test
    void testAddItemDescriptionIsNull() {
        User owner = new User();
        owner.setName("Owner4");
        owner.setEmail("owner4@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName("Valid Name");
        dto.setDescription(null);
        dto.setAvailable(true);

        User finalOwner = owner;
        assertThrows(BadRequestException.class, () ->
                itemService.addItem(finalOwner.getId(), dto)
        );
    }

    @Test
    void testAddItemDescriptionIsEmpty() {
        User owner = new User();
        owner.setName("Owner5");
        owner.setEmail("owner5@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName("Valid Name");
        dto.setDescription("");
        dto.setAvailable(true);

        User finalOwner = owner;
        assertThrows(BadRequestException.class, () ->
                itemService.addItem(finalOwner.getId(), dto)
        );
    }

    @Test
    void testAddItemAvailableIsNull() {
        User owner = new User();
        owner.setName("Owner6");
        owner.setEmail("owner6@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName("Valid Name");
        dto.setDescription("Valid Desc");
        dto.setAvailable(null); // null

        User finalOwner = owner;
        assertThrows(BadRequestException.class, () ->
                itemService.addItem(finalOwner.getId(), dto)
        );
    }

    @Test
    void testAddItemSuccessWithoutRequest() {
        User owner = new User();
        owner.setName("Owner7");
        owner.setEmail("owner7@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName("Valid Name");
        dto.setDescription("Valid Desc");
        dto.setAvailable(true);

        ItemDto created = itemService.addItem(owner.getId(), dto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Valid Name");
        assertThat(created.getDescription()).isEqualTo("Valid Desc");
        assertThat(created.getAvailable()).isTrue();
        assertThat(created.getRequestId()).isNull();

        Optional<Item> found = itemRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Valid Name");
        assertThat(found.get().getDescription()).isEqualTo("Valid Desc");
        assertThat(found.get().getRequest()).isNull();
    }

    @Test
    void testAddItemSuccessWithRequest() {
        User owner = new User();
        owner.setName("Owner8");
        owner.setEmail("owner8@example.com");
        owner = userRepository.save(owner);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need something");
        request.setRequestor(owner);
        request.setCreated(java.time.LocalDate.now().atStartOfDay());
        request = itemRequestRepository.save(request);

        ItemDto dto = new ItemDto();
        dto.setName("Valid Name with Request");
        dto.setDescription("Valid Desc with Request");
        dto.setAvailable(true);
        dto.setRequestId(request.getId());

        ItemDto created = itemService.addItem(owner.getId(), dto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Valid Name with Request");
        assertThat(created.getDescription()).isEqualTo("Valid Desc with Request");
        assertThat(created.getAvailable()).isTrue();
        assertThat(created.getRequestId()).isEqualTo(request.getId());

        Optional<Item> found = itemRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRequest().getId()).isEqualTo(request.getId());
    }

    @Test
    void testUpdateItemSuccess() {
        User owner = new User();
        owner.setName("Owner Update");
        owner.setEmail("owner.update@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName("Old Name");
        dto.setDescription("Old Description");
        dto.setAvailable(true);
        ItemDto created = itemService.addItem(owner.getId(), dto);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");

        ItemDto updated = itemService.updateItem(created.getId(), owner.getId(), updateDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getDescription()).isEqualTo("New Description");
        assertThat(updated.getAvailable()).isTrue();
    }

    @Test
    void testUpdateItemNotFound() {
        Long nonExistentItemId = 999L;
        Long userId = 1L;
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(nonExistentItemId, userId, updateDto)
        );
    }

    @Test
    void testUpdateItemForbidden() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        Item item = new Item();
        item.setName("Original Item");
        item.setDescription("Original Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Should Not Update");

        Item finalItem = item;
        User finalOtherUser = otherUser;
        assertThrows(ForbiddenException.class, () ->
                itemService.updateItem(finalItem.getId(), finalOtherUser.getId(), updateDto)
        );
    }

    @Test
    void testUpdateItemAvailable() {
        User owner = new User();
        owner.setName("Owner3");
        owner.setEmail("owner3@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Some Name");
        item.setDescription("Some Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);

        ItemDto updated = itemService.updateItem(item.getId(), owner.getId(), updateDto);

        assertThat(updated.getId()).isEqualTo(item.getId());
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void testGetItemSuccess() {
        User owner = new User();
        owner.setName("Owner Get");
        owner.setEmail("owner.get@example.com");
        owner = userRepository.save(owner);

        ItemDto dto = new ItemDto();
        dto.setName("Item Get");
        dto.setDescription("Description Get");
        dto.setAvailable(true);
        ItemDto created = itemService.addItem(owner.getId(), dto);

        ItemDto found = itemService.getItem(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Item Get");
        assertThat(found.getDescription()).isEqualTo("Description Get");
        assertThat(found.getAvailable()).isTrue();
        assertThat(found.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void testSearchItemsReturnsMatchingItems() {
        User owner = new User();
        owner.setName("Owner Search");
        owner.setEmail("owner.search@example.com");
        owner = userRepository.save(owner);

        Item item1 = new Item();
        item1.setName("Laptop Pro");
        item1.setDescription("High performance laptop");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Notebook");
        item2.setDescription("This is a laptop computer");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Old Laptop");
        item3.setDescription("Outdated laptop");
        item3.setAvailable(false);
        item3.setOwner(owner);
        itemRepository.save(item3);

        List<ItemDto> result = itemService.searchItems("laptop");

        assertThat(result).hasSize(3);
    }

    @Test
    void testGetItemsByOwner() {
        User owner = new User();
        owner.setName("Owner Items");
        owner.setEmail("owner.items@example.com");
        owner = userRepository.save(owner);

        ItemDto dto1 = new ItemDto();
        dto1.setName("Item One");
        dto1.setDescription("First Item");
        dto1.setAvailable(true);
        itemService.addItem(owner.getId(), dto1);

        ItemDto dto2 = new ItemDto();
        dto2.setName("Item Two");
        dto2.setDescription("Second Item");
        dto2.setAvailable(true);
        itemService.addItem(owner.getId(), dto2);

        List<ItemDto> items = itemService.getItemsByOwner(owner.getId());

        assertThat(items).hasSize(2);
        assertThat(items).extracting("name")
                .containsExactlyInAnyOrder("Item One", "Item Two");
    }

    @Test
    void testAddCommentSuccess() {
        User owner = new User();
        owner.setName("Owner Comment");
        owner.setEmail("owner.comment@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Comment");
        booker.setEmail("booker.comment@example.com");
        booker = userRepository.save(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Commented Item");
        itemDto.setDescription("Item for commenting");
        itemDto.setAvailable(true);
        ItemDto createdItem = itemService.addItem(owner.getId(), itemDto);

        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setStart(now.minusDays(2));
        booking.setEnd(now.minusDays(1));
        booking.setStatus(Status.APPROVED);
        Item item = itemRepository.findById(createdItem.getId()).orElseThrow();
        booking.setItem(item);
        booking.setBooker(booker);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto createdComment = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getId()).isNotNull();
        assertThat(createdComment.getText()).isEqualTo("Great item!");
        assertThat(createdComment.getAuthorName()).isEqualTo(booker.getName());
    }

    @Test
    void testAddCommentUserNotFound() {
        Long nonExistentUserId = 999L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Some comment");

        assertThrows(NotFoundException.class, () ->
                itemService.addComment(nonExistentUserId, itemId, commentDto)
        );
    }

    @Test
    void testAddCommentItemNotFound() {
        User user = new User();
        user.setName("Commenter");
        user.setEmail("commenter@example.com");
        user = userRepository.save(user);

        Long nonExistentItemId = 999L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Some comment");

        User finalUser = user;
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(finalUser.getId(), nonExistentItemId, commentDto)
        );
    }

    @Test
    void testAddCommentNoPastBooking() {
        User user = new User();
        user.setName("Commenter2");
        user.setEmail("commenter2@example.com");
        user = userRepository.save(user);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("No past booking comment");

        User finalUser = user;
        Item finalItem = item;
        assertThrows(BadRequestException.class, () ->
                itemService.addComment(finalUser.getId(), finalItem.getId(), commentDto)
        );
    }

    @Test
    void testSearchItemsReturnsEmptyWhenTextIsNullOrEmpty() {
        assertThat(itemService.searchItems(null)).isEmpty();
        assertThat(itemService.searchItems("")).isEmpty();
        assertThat(itemService.searchItems("   ")).isEmpty();
    }

}
