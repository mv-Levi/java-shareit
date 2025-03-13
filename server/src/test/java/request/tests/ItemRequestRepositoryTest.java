package request.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void testFindByRequestorIdOrderByCreatedDesc() {
        User requestor = new User();
        requestor.setName("Requestor One");
        requestor.setEmail("requestor.one@example.com");
        requestor = userRepository.save(requestor);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("First request");
        request1.setRequestor(requestor);
        request1.setCreated(LocalDateTime.now().minusHours(1));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Second request");
        request2.setRequestor(requestor);
        request2.setCreated(LocalDateTime.now().minusHours(2));
        itemRequestRepository.save(request2);

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreated()).isAfterOrEqualTo(result.get(1).getCreated());
        assertThat(result.get(0).getDescription()).isEqualTo("First request");
        assertThat(result.get(1).getDescription()).isEqualTo("Second request");
    }

    @Test
    void testFindByRequestorIdNotOrderByCreatedDesc() {
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user.one@example.com");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user.two@example.com");
        user2 = userRepository.save(user2);

        ItemRequest requestUser1 = new ItemRequest();
        requestUser1.setDescription("User1 request");
        requestUser1.setRequestor(user1);
        requestUser1.setCreated(LocalDateTime.now().minusHours(1));
        itemRequestRepository.save(requestUser1);

        ItemRequest requestUser2a = new ItemRequest();
        requestUser2a.setDescription("User2 first request");
        requestUser2a.setRequestor(user2);
        requestUser2a.setCreated(LocalDateTime.now().minusHours(2));
        itemRequestRepository.save(requestUser2a);

        ItemRequest requestUser2b = new ItemRequest();
        requestUser2b.setDescription("User2 second request");
        requestUser2b.setRequestor(user2);
        requestUser2b.setCreated(LocalDateTime.now().minusHours(3));
        itemRequestRepository.save(requestUser2b);

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(user1.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreated()).isAfterOrEqualTo(result.get(1).getCreated());
        assertThat(result.get(0).getRequestor().getId()).isEqualTo(user2.getId());
        assertThat(result.get(1).getRequestor().getId()).isEqualTo(user2.getId());
    }

    @Test
    void testGetUserRequests() {
        User requestor = new User();
        requestor.setName("Requestor");
        requestor.setEmail("req@example.com");
        requestor = userRepository.save(requestor);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        ItemRequest req1 = new ItemRequest();
        req1.setDescription("Need a laptop");
        req1.setRequestor(requestor);
        req1.setCreated(LocalDateTime.now().minusHours(1));
        req1 = itemRequestRepository.save(req1);

        ItemRequest req2 = new ItemRequest();
        req2.setDescription("Need a guitar");
        req2.setRequestor(requestor);
        req2.setCreated(LocalDateTime.now().minusHours(2));
        req2 = itemRequestRepository.save(req2);

        Item item1 = new Item();
        item1.setName("Laptop Pro");
        item1.setDescription("A powerful laptop");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1.setRequest(req1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Laptop Air");
        item2.setDescription("A lightweight laptop");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2.setRequest(req1);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Electric Guitar");
        item3.setDescription("A rock guitar");
        item3.setAvailable(true);
        item3.setOwner(owner);
        item3.setRequest(req2);
        itemRepository.save(item3);

        List<ItemRequestDto> result = itemRequestService.getUserRequests(requestor.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(req1.getId());
        assertThat(result.get(1).getId()).isEqualTo(req2.getId());

        List<ItemShortDto> responsesForReq1 = result.get(0).getItems();
        assertThat(responsesForReq1).hasSize(2);
        assertThat(responsesForReq1).extracting("name")
                .containsExactlyInAnyOrder("Laptop Pro", "Laptop Air");

        List<ItemShortDto> responsesForReq2 = result.get(1).getItems();
        assertThat(responsesForReq2).hasSize(1);
        assertThat(responsesForReq2.get(0).getName()).isEqualTo("Electric Guitar");
    }

    @Test
    void testGetUserRequestsNoRequests() {
        User requestor = new User();
        requestor.setName("NoRequestsUser");
        requestor.setEmail("noreq@example.com");
        requestor = userRepository.save(requestor);

        List<ItemRequestDto> result = itemRequestService.getUserRequests(requestor.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void testGetUserRequestsNoItems() {
        User requestor = new User();
        requestor.setName("RequestorNoItems");
        requestor.setEmail("reqNoItems@example.com");
        requestor = userRepository.save(requestor);

        ItemRequest req = new ItemRequest();
        req.setDescription("Need something");
        req.setRequestor(requestor);
        req.setCreated(LocalDateTime.now());
        req = itemRequestRepository.save(req);

        List<ItemRequestDto> result = itemRequestService.getUserRequests(requestor.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(req.getId());
        assertThat(result.get(0).getItems()).isEmpty();
    }
}
