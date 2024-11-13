package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    // Преобразование из Item в ItemDto
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());                      // Устанавливаем id
        itemDto.setName(item.getName());                   // Устанавливаем название
        itemDto.setDescription(item.getDescription());     // Устанавливаем описание
        itemDto.setAvailable(item.getAvailable());         // Устанавливаем статус доступности
        itemDto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null); // Устанавливаем ID владельца
        itemDto.setIsRequest(item.getRequest() != null);   // Проверяем, был ли запрос

        return itemDto;
    }


    // Преобразование из ItemDto в Item
    public static Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        Item item = new Item();

        item.setId(itemDto.getId());               // Устанавливаем id
        item.setName(itemDto.getName());           // Устанавливаем название
        item.setDescription(itemDto.getDescription()); // Устанавливаем описание
        item.setAvailable(itemDto.getAvailable()); // Устанавливаем доступность
        item.setOwner(owner);                      // Устанавливаем владельца
        item.setRequest(request);                  // Устанавливаем запрос, если он есть

        return item;
    }
}
