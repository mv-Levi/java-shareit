package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemShortDto {
    private Long id;
    private String name;
    private Long ownerId;
}
