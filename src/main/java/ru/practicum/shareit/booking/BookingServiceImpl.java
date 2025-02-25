package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import org.slf4j.Logger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        Long itemId = bookingDto.getItemId();
        if (itemId == null) {
            throw new NotFoundException("Item not found with ID: null");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + itemId));

        if (!item.getAvailable()) {
            throw new BadRequestException("Item with ID: " + itemId + " is not available for booking.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Owner cannot book their own item.");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new BadRequestException("Start and end dates must be provided.");
        }

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new BadRequestException("Start date must be before end date.");
        }

        Booking booking = BookingMapper.toEntity(bookingDto, item, booker);
        booking.setStatus(Status.WAITING);
        log.info("Received bookingDto: {}", bookingDto);
        log.info("bookingDto.getItemId(): {}", bookingDto.getItemId());

        booking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only the owner can change the booking status.");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException("Booking status is already decided.");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        booking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Access denied.");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }

        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case "FUTURE" ->
                    bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case "WAITING" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        };

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}