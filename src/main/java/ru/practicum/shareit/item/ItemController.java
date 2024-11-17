package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.logging.Logger;


/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final Logger log = Logger.getLogger(ItemController.class.getName());
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Добавление новой вещи
    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("Adding item for user ID: " + userId + " with data: " + itemDto);
        return itemService.addItem(userId, itemDto);
    }

    // Редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("Updating item with ID: " + itemId + " for user ID: " + userId + " with data: " + itemDto);
        return itemService.updateItem(itemId, userId, itemDto);
    }

    // Получение информации о вещи
    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Fetching item with ID: " + itemId);
        return itemService.getItem(itemId);
    }

    // Получение всех вещей пользователя
    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Fetching all items for user ID: " + userId);
        return itemService.getItemsByOwner(userId);
    }

    // Поиск вещи
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Searching for items with text: " + text);
        return itemService.searchItems(text);
    }
}
