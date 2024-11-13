package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequest {
    Long id;
    String description;
    private User requestor;
    private LocalDate created;
}
