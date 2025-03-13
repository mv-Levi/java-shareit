package booking.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item createItemForOwner(User owner) {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Test
    void testCreateBookingHappyPath() {
        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getStatus()).isEqualTo(Status.WAITING);
        assertThat(createdBooking.getStart()).isEqualTo(start);
        assertThat(createdBooking.getEnd()).isEqualTo(end);
        assertThat(createdBooking.getItem()).isNotNull();
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking.getBooker()).isNotNull();
        assertThat(createdBooking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void testUpdateBookingStatusApprovedHappyPath() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        booking = bookingRepository.save(booking);

        BookingDto updatedBooking = bookingService.updateBookingStatus(booking.getId(), true, owner.getId());

        assertThat(updatedBooking.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void testUpdateBookingStatusRejectedHappyPath() {
        User owner = new User();
        owner.setName("Owner2");
        owner.setEmail("owner2@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker2");
        booker.setEmail("booker2@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item 2");
        item.setDescription("Test Description 2");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(4));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        booking = bookingRepository.save(booking);

        BookingDto updatedBooking = bookingService.updateBookingStatus(booking.getId(), false, owner.getId());

        assertThat(updatedBooking.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testGetBookingByIdSuccessForBooker() {
        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        booking = bookingRepository.save(booking);

        BookingDto result = bookingService.getBookingById(booking.getId(), booker.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetBookingByIdSuccessForOwner() {
        User booker = new User();
        booker.setName("Booker2");
        booker.setEmail("booker2@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner2");
        owner.setEmail("owner2@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item2");
        item.setDescription("Item Description 2");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        booking = bookingRepository.save(booking);

        BookingDto result = bookingService.getBookingById(booking.getId(), owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    void testGetBookingByIdAccessDenied() {
        User booker = new User();
        booker.setName("Booker3");
        booker.setEmail("booker3@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner3");
        owner.setEmail("owner3@example.com");
        owner = userRepository.save(owner);

        User other = new User();
        other.setName("Other");
        other.setEmail("other@example.com");
        other = userRepository.save(other);

        Item item = new Item();
        item.setName("Item3");
        item.setDescription("Item Description 3");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(4));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        booking = bookingRepository.save(booking);

        Booking finalBooking = booking;
        User finalOther = other;
        assertThrows(ForbiddenException.class, () -> {
            bookingService.getBookingById(finalBooking.getId(), finalOther.getId());
        });
    }

    @Test
    void testGetBookingsByUserCurrent() {
        User booker = new User();
        booker.setName("Booker Current");
        booker.setEmail("booker.current@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner Current");
        owner.setEmail("owner.current@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item Current");
        item.setDescription("Description Current");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(2));
        currentBooking.setEnd(now.plusHours(2));
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking = bookingRepository.save(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusHours(4));
        pastBooking.setEnd(now.minusHours(3));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusHours(3));
        futureBooking.setEnd(now.plusHours(4));
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        bookingRepository.save(futureBooking);

        List<BookingDto> currentBookings = bookingService.getBookingsByUser(booker.getId(), "CURRENT");

        assertThat(currentBookings).hasSize(1);
        assertThat(currentBookings.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void testGetBookingsByUserFuture() {
        User booker = new User();
        booker.setName("Booker Future");
        booker.setEmail("booker.future@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner Future");
        owner.setEmail("owner.future@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item Future");
        item.setDescription("Description Future");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking futureBooking1 = new Booking();
        futureBooking1.setStart(now.plusHours(3));
        futureBooking1.setEnd(now.plusHours(4));
        futureBooking1.setStatus(Status.APPROVED);
        futureBooking1.setItem(item);
        futureBooking1.setBooker(booker);
        futureBooking1 = bookingRepository.save(futureBooking1);

        Booking futureBooking2 = new Booking();
        futureBooking2.setStart(now.plusHours(5));
        futureBooking2.setEnd(now.plusHours(6));
        futureBooking2.setStatus(Status.APPROVED);
        futureBooking2.setItem(item);
        futureBooking2.setBooker(booker);
        futureBooking2 = bookingRepository.save(futureBooking2);

        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(2));
        currentBooking.setEnd(now.plusHours(2));
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        bookingRepository.save(currentBooking);

        List<BookingDto> futureBookings = bookingService.getBookingsByUser(booker.getId(), "FUTURE");

        assertThat(futureBookings).hasSize(2);
        assertThat(futureBookings.get(0).getId()).isEqualTo(futureBooking2.getId());
        assertThat(futureBookings.get(1).getId()).isEqualTo(futureBooking1.getId());
    }

    @Test
    void testGetBookingsByOwnerCurrent() {
        User owner = new User();
        owner.setName("Owner Current");
        owner.setEmail("owner.current@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Current");
        booker.setEmail("booker.current@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Item Current");
        item.setDescription("Description Current");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(2));
        currentBooking.setEnd(now.plusHours(2));
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking = bookingRepository.save(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusHours(3));
        futureBooking.setEnd(now.plusHours(4));
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        bookingRepository.save(futureBooking);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "CURRENT");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void testGetBookingsByOwnerFuture() {
        User owner = new User();
        owner.setName("Owner Future");
        owner.setEmail("owner.future@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Future");
        booker.setEmail("booker.future@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Item Future");
        item.setDescription("Description Future");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking futureBooking1 = new Booking();
        futureBooking1.setStart(now.plusHours(2));
        futureBooking1.setEnd(now.plusHours(3));
        futureBooking1.setStatus(Status.APPROVED);
        futureBooking1.setItem(item);
        futureBooking1.setBooker(booker);
        futureBooking1 = bookingRepository.save(futureBooking1);

        Booking futureBooking2 = new Booking();
        futureBooking2.setStart(now.plusHours(4));
        futureBooking2.setEnd(now.plusHours(5));
        futureBooking2.setStatus(Status.APPROVED);
        futureBooking2.setItem(item);
        futureBooking2.setBooker(booker);
        futureBooking2 = bookingRepository.save(futureBooking2);

        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        bookingRepository.save(currentBooking);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "FUTURE");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(futureBooking2.getId());
        assertThat(result.get(1).getId()).isEqualTo(futureBooking1.getId());
    }

    @Test
    void testCurrentState() {
        User owner = new User(null, "Owner Current", "owner.current@example.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker Current", "booker.current@example.com");
        booker = userRepository.save(booker);

        Item item = createItemForOwner(owner);

        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(2));
        currentBooking.setEnd(now.plusHours(2));
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking = bookingRepository.save(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusHours(3));
        futureBooking.setEnd(now.plusHours(4));
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        bookingRepository.save(futureBooking);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "CURRENT");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void testPastState() {
        User owner = new User(null, "Owner Past", "owner.past@example.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker Past", "booker.past@example.com");
        booker = userRepository.save(booker);

        Item item = createItemForOwner(owner);
        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setStart(now.minusDays(3));
        booking1.setEnd(now.minusDays(2));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1 = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.minusDays(2));
        booking2.setEnd(now.minusDays(1));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2 = bookingRepository.save(booking2);

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(1).plusHours(1));
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        bookingRepository.save(futureBooking);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "PAST");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(result.get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    void testFutureState() {
        User owner = new User(null, "Owner Future", "owner.future2@example.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker Future", "booker.future2@example.com");
        booker = userRepository.save(booker);

        Item item = createItemForOwner(owner);
        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setStart(now.plusHours(2));
        booking1.setEnd(now.plusHours(3));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1 = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusHours(4));
        booking2.setEnd(now.plusHours(5));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2 = bookingRepository.save(booking2);

        Booking currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));
        currentBooking.setStatus(Status.APPROVED);
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        bookingRepository.save(currentBooking);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "FUTURE");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(result.get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    void testWaitingState() {
        User owner = new User(null, "Owner Waiting", "owner.waiting@example.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker Waiting", "booker.waiting@example.com");
        booker = userRepository.save(booker);

        Item item = createItemForOwner(owner);
        LocalDateTime now = LocalDateTime.now();

        Booking bookingWaiting = new Booking();
        bookingWaiting.setStart(now.plusDays(1));
        bookingWaiting.setEnd(now.plusDays(1).plusHours(2));
        bookingWaiting.setStatus(Status.WAITING);
        bookingWaiting.setItem(item);
        bookingWaiting.setBooker(booker);
        bookingWaiting = bookingRepository.save(bookingWaiting);

        Booking bookingApproved = new Booking();
        bookingApproved.setStart(now.plusDays(2));
        bookingApproved.setEnd(now.plusDays(2).plusHours(2));
        bookingApproved.setStatus(Status.APPROVED);
        bookingApproved.setItem(item);
        bookingApproved.setBooker(booker);
        bookingRepository.save(bookingApproved);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "WAITING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(bookingWaiting.getId());
    }

    @Test
    void testRejectedState() {
        User owner = new User(null, "Owner Rejected", "owner.rejected@example.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker Rejected", "booker.rejected@example.com");
        booker = userRepository.save(booker);

        Item item = createItemForOwner(owner);
        LocalDateTime now = LocalDateTime.now();

        Booking bookingRejected = new Booking();
        bookingRejected.setStart(now.plusDays(3));
        bookingRejected.setEnd(now.plusDays(3).plusHours(2));
        bookingRejected.setStatus(Status.REJECTED);
        bookingRejected.setItem(item);
        bookingRejected.setBooker(booker);
        bookingRejected = bookingRepository.save(bookingRejected);

        Booking bookingWaiting = new Booking();
        bookingWaiting.setStart(now.plusDays(4));
        bookingWaiting.setEnd(now.plusDays(4).plusHours(2));
        bookingWaiting.setStatus(Status.WAITING);
        bookingWaiting.setItem(item);
        bookingWaiting.setBooker(booker);
        bookingRepository.save(bookingWaiting);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "REJECTED");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(bookingRejected.getId());
    }

    @Test
    void testDefaultState_All() {
        User owner = new User(null, "Owner All", "owner.all@example.com");
        owner = userRepository.save(owner);

        User booker = new User(null, "Booker All", "booker.all@example.com");
        booker = userRepository.save(booker);

        Item item = createItemForOwner(owner);
        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now.plusHours(1));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setBooker(booker);
        booking1 = bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.minusDays(2));
        booking2.setEnd(now.minusDays(1));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2 = bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(now.plusDays(1));
        booking3.setEnd(now.plusDays(1).plusHours(1));
        booking3.setStatus(Status.APPROVED);
        booking3.setItem(item);
        booking3.setBooker(booker);
        bookingRepository.save(booking3);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "ALL");

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getStart()).isAfterOrEqualTo(result.get(1).getStart());
        assertThat(result.get(1).getStart()).isAfterOrEqualTo(result.get(2).getStart());
    }

    @Test
    void testCreateBookingUserNotFound() {
        Long nonExistentUserId = 999L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(nonExistentUserId, bookingDto)
        );
    }

    @Test
    void testCreateBookingItemIdNull() {
        User user = new User();
        user.setName("Booker");
        user.setEmail("booker@example.com");
        user = userRepository.save(user);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(null);

        User finalUser = user;
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(finalUser.getId(), bookingDto)
        );
    }

    @Test
    void testCreateBookingItemNotFound() {
        User user = new User();
        user.setName("Booker2");
        user.setEmail("booker2@example.com");
        user = userRepository.save(user);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(999L);

        User finalUser = user;
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(finalUser.getId(), bookingDto)
        );
    }

    @Test
    void testCreateBookingItemNotAvailable() {
        User user = new User();
        user.setName("Booker3");
        user.setEmail("booker3@example.com");
        user = userRepository.save(user);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Unavailable Item");
        item.setDescription("Desc");
        item.setAvailable(false);
        item.setOwner(owner);
        item = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());

        User finalUser = user;
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(finalUser.getId(), bookingDto)
        );
    }

    @Test
    void testCreateBookingOwnerCannotBookOwnItem() {
        User owner = new User();
        owner.setName("Owner2");
        owner.setEmail("owner2@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Available Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());

        User finalOwner = owner;
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(finalOwner.getId(), bookingDto)
        );
    }

    @Test
    void testCreateBookingStartOrEndNull() {
        User booker = new User();
        booker.setName("Booker4");
        booker.setEmail("booker4@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner3");
        owner.setEmail("owner3@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Available Item2");
        item.setDescription("Desc2");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(null);
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        User finalBooker = booker;
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(finalBooker.getId(), bookingDto)
        );
    }

    @Test
    void testCreateBookingStartNotBeforeEnd() {
        User booker = new User();
        booker.setName("Booker5");
        booker.setEmail("booker5@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner4");
        owner.setEmail("owner4@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item3");
        item.setDescription("Desc3");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        User finalBooker = booker;
        assertThrows(BadRequestException.class, () ->
                bookingService.createBooking(finalBooker.getId(), bookingDto)
        );
    }

    @Test
    void testUpdateBookingStatusNotFound() {
        // Arrange
        Long nonExistentBookingId = 999L;
        boolean approved = true;
        Long userId = 1L;

        assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(nonExistentBookingId, approved, userId)
        );
    }

    @Test
    void testUpdateBookingStatusForbidden() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        boolean approved = true;
        Booking finalBooking = booking;
        User finalOtherUser = otherUser;
        assertThrows(ForbiddenException.class, () ->
                bookingService.updateBookingStatus(finalBooking.getId(), approved, finalOtherUser.getId())
        );
    }

    @Test
    void testUpdateBookingStatusAlreadyDecided() {
        User owner = new User();
        owner.setName("Owner2");
        owner.setEmail("owner2@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Test Item2");
        item.setDescription("Desc2");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        User booker = new User();
        booker.setName("Booker2");
        booker.setEmail("booker2@example.com");
        booker = userRepository.save(booker);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.APPROVED);
        booking = bookingRepository.save(booking);

        boolean approved = false;
        Booking finalBooking = booking;
        User finalOwner = owner;
        assertThrows(BadRequestException.class, () ->
                bookingService.updateBookingStatus(finalBooking.getId(), approved, finalOwner.getId())
        );
    }

    @Test
    void testUpdateBookingStatusApproved() {
        User owner = new User();
        owner.setName("Owner3");
        owner.setEmail("owner3@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker3");
        booker.setEmail("booker3@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Available Item");
        item.setDescription("Desc3");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        boolean approved = true;
        BookingDto updated = bookingService.updateBookingStatus(booking.getId(), approved, owner.getId());

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(booking.getId());
        assertThat(updated.getStatus()).isEqualTo(Status.APPROVED);
        Booking saved = bookingRepository.findById(updated.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void testUpdateBookingStatusRejected() {
        User owner = new User();
        owner.setName("Owner4");
        owner.setEmail("owner4@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker4");
        booker.setEmail("booker4@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Available Item4");
        item.setDescription("Desc4");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);

        boolean approved = false;
        BookingDto updated = bookingService.updateBookingStatus(booking.getId(), approved, owner.getId());

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(booking.getId());
        assertThat(updated.getStatus()).isEqualTo(Status.REJECTED);
        Booking saved = bookingRepository.findById(updated.getId()).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testGetBookingsByUserUserNotFound() {
        Long nonExistentUserId = 999L;

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsByUser(nonExistentUserId, "ALL")
        );
    }

    @Test
    void testGetBookingsByUserPast() {
        User booker = createUser("Booker2", "booker2@example.com");
        User owner = createUser("Owner2", "owner2@example.com");
        Item item = createItem("Item2", "Desc2", true, owner);

        Booking past1 = createBooking(booker, item,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2),
                Status.APPROVED);

        Booking past2 = createBooking(booker, item,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                Status.APPROVED);

        createBooking(booker, item,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusHours(1),
                Status.APPROVED);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "PAST");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(past2.getId());
        assertThat(result.get(1).getId()).isEqualTo(past1.getId());
    }

    @Test
    void testGetBookingsByUserWaiting() {
        User booker = createUser("Booker4", "booker4@example.com");
        User owner = createUser("Owner4", "owner4@example.com");
        Item item = createItem("Item4", "Desc4", true, owner);

        Booking waiting1 = createBooking(booker, item,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                Status.WAITING);

        Booking waiting2 = createBooking(booker, item,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                Status.WAITING);

        createBooking(booker, item,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                Status.APPROVED);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "WAITING");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(waiting2.getId());
        assertThat(result.get(1).getId()).isEqualTo(waiting1.getId());
    }

    @Test
    void testGetBookingsByUserRejected() {
        User booker = createUser("Booker5", "booker5@example.com");
        User owner = createUser("Owner5", "owner5@example.com");
        Item item = createItem("Item5", "Desc5", true, owner);

        Booking rejected1 = createBooking(booker, item,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                Status.REJECTED);

        Booking rejected2 = createBooking(booker, item,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                Status.REJECTED);

        createBooking(booker, item,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                Status.WAITING);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "REJECTED");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(rejected2.getId());
        assertThat(result.get(1).getId()).isEqualTo(rejected1.getId());
    }

    @Test
    void testGetBookingsByUserAll() {
        User booker = createUser("Booker6", "booker6@example.com");
        User owner = createUser("Owner6", "owner6@example.com");
        Item item = createItem("Item6", "Desc6", true, owner);

        Booking b1 = createBooking(booker, item,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                Status.APPROVED);

        Booking b2 = createBooking(booker, item,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4),
                Status.WAITING);

        Booking b3 = createBooking(booker, item,
                LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(6),
                Status.REJECTED);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "ALL");

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(b3.getId());
        assertThat(result.get(1).getId()).isEqualTo(b2.getId());
        assertThat(result.get(2).getId()).isEqualTo(b1.getId());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, String desc, boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(desc);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, LocalDateTime start, LocalDateTime end, Status status) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Test
    void testGetBookingsByOwnerUserNotFound() {
        Long nonExistentUserId = 999L;
        String state = "ALL";

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsByOwner(nonExistentUserId, state)
        );
    }

}
