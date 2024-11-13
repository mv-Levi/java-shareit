package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> itemDatabase = new HashMap<>();
    private final UserService userService;
    private long idCounter = 1;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Field 'available' is required.");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new BadRequestException("Field 'name' is required.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new BadRequestException("Field 'description' is required.");
        }

        if (userService.getUser(userId) == null) {
            throw new NotFoundException("User not found.");
        }

        Item item = ItemMapper.toItem(itemDto, new User(userId, "Owner", "owner@example.com"), null);
        item.setId(idCounter++);
        itemDatabase.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemDatabase.get(itemId);

        if (item == null) {
            throw new NotFoundException("Item not found.");
        }

        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to update this item.");
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemDatabase.get(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found.");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemDatabase.values()) {
            if (item.getOwner().getId().equals(userId)) {
                items.add(ItemMapper.toItemDto(item));
            }
        }
        return items;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> foundItems = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return foundItems;
        }

        for (Item item : itemDatabase.values()) {
            if (item.getAvailable() &&
                    (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                foundItems.add(ItemMapper.toItemDto(item));
            }
        }

        return foundItems;
    }
}