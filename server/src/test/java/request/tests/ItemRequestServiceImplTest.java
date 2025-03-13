package request.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requestor;

    @Test
    void testCreateRequestSuccess() {
        User requestor = new User();
        requestor.setName("Requestor Test");
        requestor.setEmail("requestor@test.com");
        requestor = userRepository.save(requestor);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("I need a laptop");

        ItemRequestDto createdDto = itemRequestService.createRequest(requestor.getId(), requestDto);

        assertThat(createdDto).isNotNull();
        assertThat(createdDto.getId()).isNotNull();
        assertThat(createdDto.getDescription()).isEqualTo("I need a laptop");
        assertThat(createdDto.getCreated()).isNotNull();
        assertThat(createdDto.getItems()).isEmpty();
    }

    @Test
    void testGetUserRequests() throws InterruptedException {
        User requestor = new User();
        requestor.setName("Requestor User");
        requestor.setEmail("req.user@example.com");
        requestor = userRepository.save(requestor);

        ItemRequestDto dto1 = new ItemRequestDto();
        dto1.setDescription("First request");
        ItemRequestDto created1 = itemRequestService.createRequest(requestor.getId(), dto1);

        // Немного задержимся, чтобы у запросов различное время создания
        Thread.sleep(10);

        ItemRequestDto dto2 = new ItemRequestDto();
        dto2.setDescription("Second request");
        ItemRequestDto created2 = itemRequestService.createRequest(requestor.getId(), dto2);

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(requestor.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Second request");
        assertThat(requests.get(1).getDescription()).isEqualTo("First request");
    }

    @Test
    void testGetAllRequests() {
        User requestor = new User();
        requestor.setName("Main Requestor");
        requestor.setEmail("mainreq@example.com");
        requestor = userRepository.save(requestor);

        User other = new User();
        other.setName("Other User");
        other.setEmail("other@example.com");
        other = userRepository.save(other);

        ItemRequest mainReq = new ItemRequest();
        mainReq.setDescription("Request from main requestor");
        mainReq.setRequestor(requestor);
        mainReq.setCreated(LocalDateTime.now().minusHours(3));
        itemRequestRepository.save(mainReq);

        ItemRequest otherReq1 = new ItemRequest();
        otherReq1.setDescription("Other request 1");
        otherReq1.setRequestor(other);
        otherReq1.setCreated(LocalDateTime.now().minusHours(2));
        otherReq1 = itemRequestRepository.save(otherReq1);

        ItemRequest otherReq2 = new ItemRequest();
        otherReq2.setDescription("Other request 2");
        otherReq2.setRequestor(other);
        otherReq2.setCreated(LocalDateTime.now().minusHours(4));
        otherReq2 = itemRequestRepository.save(otherReq2);

        Item item1 = new Item();
        item1.setName("Item for otherReq1");
        item1.setDescription("Description for item1");
        item1.setAvailable(true);
        item1.setOwner(other);
        item1.setRequest(otherReq1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item for otherReq2");
        item2.setDescription("Description for item2");
        item2.setAvailable(true);
        item2.setOwner(other);
        item2.setRequest(otherReq2);
        itemRepository.save(item2);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(requestor.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(otherReq1.getId());
        assertThat(result.get(1).getId()).isEqualTo(otherReq2.getId());

        List<ItemShortDto> itemsForReq1 = result.get(0).getItems();
        assertThat(itemsForReq1).hasSize(1);
        assertThat(itemsForReq1.get(0).getName()).isEqualTo("Item for otherReq1");

        List<ItemShortDto> itemsForReq2 = result.get(1).getItems();
        assertThat(itemsForReq2).hasSize(1);
        assertThat(itemsForReq2.get(0).getName()).isEqualTo("Item for otherReq2");
    }

    @Test
    void testGetAllRequestsWhenNoOtherRequests() {
        User requestor = new User();
        requestor.setName("Solo Requestor");
        requestor.setEmail("solo@example.com");
        requestor = userRepository.save(requestor);

        ItemRequest req = new ItemRequest();
        req.setDescription("Solo request");
        req.setRequestor(requestor);
        req.setCreated(LocalDateTime.now());
        itemRequestRepository.save(req);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(requestor.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllRequestsNoItems() {
        User requestor = new User();
        requestor.setName("Main Req2");
        requestor.setEmail("mainreq2@example.com");
        requestor = userRepository.save(requestor);

        User other = new User();
        other.setName("Other2");
        other.setEmail("other2@example.com");
        other = userRepository.save(other);

        ItemRequest otherReq = new ItemRequest();
        otherReq.setDescription("Request from other2");
        otherReq.setRequestor(other);
        otherReq.setCreated(LocalDateTime.now().minusHours(1));
        otherReq = itemRequestRepository.save(otherReq);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(requestor.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(otherReq.getId());
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    void testGetRequestByIdSuccess() {
        User requestor = new User();
        requestor.setName("Requestor Single");
        requestor.setEmail("single.req@example.com");
        requestor = userRepository.save(requestor);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Single request");
        ItemRequestDto created = itemRequestService.createRequest(requestor.getId(), dto);

        ItemRequestDto result = itemRequestService.getRequestById(requestor.getId(), created.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getDescription()).isEqualTo("Single request");
    }

    @Test
    void testGetRequestByIdNotFound() {
        User requestor = new User();
        requestor.setName("Requestor NotFound");
        requestor.setEmail("notfound@example.com");
        requestor = userRepository.save(requestor);

        User finalRequestor = requestor;
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(finalRequestor.getId(), 999L);
        });
    }

    @Test
    void testGetRequestByIdUserNotFound() {
        Long nonExistentUserId = 999L;
        Long anyRequestId = 1L;

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(nonExistentUserId, anyRequestId)
        );
    }

    @Test
    void testGetRequestByIdRequestNotFound() {
        User user = new User();
        user.setName("Requestor");
        user.setEmail("req@example.com");
        user = userRepository.save(user);

        Long nonExistentRequestId = 999L;

        // Act & Assert
        User finalUser = user;
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(finalUser.getId(), nonExistentRequestId)
        );
    }

    @Test
    void testCreateRequestWithNullDescription() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription(null);

        assertThrows(RuntimeException.class, () ->
                itemRequestService.createRequest(requestor.getId(), requestDto));
    }

    @Test
    void testGetRequestByIdWithMultipleItems() {
        User requestor = new User();
        requestor.setName("Requestor Filter");
        requestor.setEmail("filter@example.com");
        requestor = userRepository.save(requestor);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Filter test request");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        Item correctItem = new Item();
        correctItem.setName("Correct item");
        correctItem.setDescription("Belongs to itemRequest");
        correctItem.setOwner(requestor);
        correctItem.setAvailable(true);
        correctItem.setRequest(itemRequest);
        itemRepository.save(correctItem);

        Item nullRequestItem = new Item();
        nullRequestItem.setName("Null request item");
        nullRequestItem.setDescription("Should not appear in result");
        nullRequestItem.setOwner(requestor);
        nullRequestItem.setAvailable(true);
        nullRequestItem.setRequest(null);
        itemRepository.save(nullRequestItem);

        User anotherUser = new User();
        anotherUser.setName("Another");
        anotherUser.setEmail("another@example.com");
        anotherUser = userRepository.save(anotherUser);

        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setDescription("Other user request");
        otherRequest.setRequestor(anotherUser);
        otherRequest.setCreated(LocalDateTime.now().minusHours(1));
        otherRequest = itemRequestRepository.save(otherRequest);

        Item differentRequestItem = new Item();
        differentRequestItem.setName("Different request item");
        differentRequestItem.setDescription("Belongs to otherRequest");
        differentRequestItem.setOwner(anotherUser);
        differentRequestItem.setAvailable(true);
        differentRequestItem.setRequest(otherRequest);
        itemRepository.save(differentRequestItem);

        ItemRequestDto result = itemRequestService.getRequestById(requestor.getId(), itemRequest.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemRequest.getId());
        assertThat(result.getItems()).hasSize(1);

        ItemShortDto shortDto = result.getItems().get(0);
        assertThat(shortDto.getId()).isEqualTo(correctItem.getId());
        assertThat(shortDto.getName()).isEqualTo("Correct item");
        assertThat(shortDto.getOwnerId()).isEqualTo(requestor.getId());
    }

}
