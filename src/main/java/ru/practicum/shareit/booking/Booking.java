package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    Long id;
    LocalDate start;
    LocalDate end;
    Item item;
    User booker;
    Status status;
}
