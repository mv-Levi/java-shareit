package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(Long requestorId, ItemRequestDto requestDto) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + requestorId));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return toDto(saved, List.of()); // На момент создания ответов нет
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long requestorId) {
        // Получаем запросы, созданные данным пользователем
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
        return requests.stream()
                .map(req -> {
                    // Для каждого запроса находим связанные вещи (отклики)
                    List<Item> items = itemRepository.findAll().stream()
                            .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(req.getId()))
                            .collect(Collectors.toList());
                    List<ItemShortDto> responses = items.stream()
                            .map(item -> {
                                ItemShortDto dto = new ItemShortDto();
                                dto.setId(item.getId());
                                dto.setName(item.getName());
                                dto.setOwnerId(item.getOwner().getId());
                                return dto;
                            })
                            .collect(Collectors.toList());
                    return toDto(req, responses);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long requestorId) {
        // Получаем запросы, созданные другими пользователями
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId);
        return requests.stream()
                .map(req -> {
                    List<Item> items = itemRepository.findAll().stream()
                            .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(req.getId()))
                            .collect(Collectors.toList());
                    List<ItemShortDto> responses = items.stream()
                            .map(item -> {
                                ItemShortDto dto = new ItemShortDto();
                                dto.setId(item.getId());
                                dto.setName(item.getName());
                                dto.setOwnerId(item.getOwner().getId());
                                return dto;
                            })
                            .collect(Collectors.toList());
                    return toDto(req, responses);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestorId, Long requestId) {
        // Проверяем, что пользователь существует
        userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + requestorId));
        ItemRequest req = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found with ID: " + requestId));

        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(req.getId()))
                .collect(Collectors.toList());
        List<ItemShortDto> responses = items.stream()
                .map(item -> {
                    ItemShortDto dto = new ItemShortDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setOwnerId(item.getOwner().getId());
                    return dto;
                })
                .collect(Collectors.toList());
        return toDto(req, responses);
    }

    // Приватный метод для маппинга ItemRequest в ItemRequestDto с заполнением ответов
    private ItemRequestDto toDto(ItemRequest request, List<ItemShortDto> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items);
        return dto;
    }
}