package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.logging.Logger;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final Logger log = Logger.getLogger(ItemController.class.getName());
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemDto itemDto) {
        log.info("Gateway: Adding item for user ID: " + userId + " with data: " + itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Gateway: Updating item with ID: " + itemId + " for user ID: " + userId
                + " with data: " + itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        log.info("Gateway: Fetching item with ID: " + itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: Fetching all items for user ID: " + userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Gateway: Searching for items with text: " + text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: Adding comment to itemId=" + itemId + " by userId=" + userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}