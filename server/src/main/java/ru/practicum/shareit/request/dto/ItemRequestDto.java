package ru.practicum.shareit.request.dto;

/**
 * TODO Sprint add-item-requests.
 */

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;
}
