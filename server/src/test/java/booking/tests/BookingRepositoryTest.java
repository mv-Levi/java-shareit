package booking.tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ru.practicum.shareit.ShareItServer.class)
@Transactional
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testFindByBookerIdOrderByStartDesc() {
        User booker = new User();
        booker.setName("Booker One");
        booker.setEmail("booker1@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner One");
        owner.setEmail("owner1@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(2).plusHours(2));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setBooker(booker);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().minusDays(1).plusHours(2));
        booking2.setStatus(Status.WAITING);
        booking2.setItem(item);
        booking2.setBooker(booker);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isAfterOrEqualTo(bookings.get(1).getStart());
    }

    @Test
    void testFindByItemOwnerIdOrderByStartDesc() {
        User owner = new User();
        owner.setName("Owner Two");
        owner.setEmail("owner2@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Two");
        booker.setEmail("booker2@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Owner's Item");
        item.setDescription("Owner's Item Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().minusDays(3));
        booking1.setEnd(LocalDateTime.now().minusDays(3).plusHours(1));
        booking1.setStatus(Status.WAITING);
        booking1.setItem(item);
        booking1.setBooker(booker);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().minusDays(1).plusHours(1));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(booker);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStart()).isAfterOrEqualTo(bookings.get(1).getStart());
    }

    @Test
    void testFindByBookerIdAndStatusOrderByStartDesc() {
        User booker = new User();
        booker.setName("Booker Three");
        booker.setEmail("booker3@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner Three");
        owner.setEmail("owner3@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item Three");
        item.setDescription("Description Three");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking bookingWaiting = new Booking();
        bookingWaiting.setStart(LocalDateTime.now().minusDays(2));
        bookingWaiting.setEnd(LocalDateTime.now().minusDays(2).plusHours(2));
        bookingWaiting.setStatus(Status.WAITING);
        bookingWaiting.setItem(item);
        bookingWaiting.setBooker(booker);
        bookingRepository.save(bookingWaiting);

        Booking bookingApproved = new Booking();
        bookingApproved.setStart(LocalDateTime.now().minusDays(1));
        bookingApproved.setEnd(LocalDateTime.now().minusDays(1).plusHours(2));
        bookingApproved.setStatus(Status.APPROVED);
        bookingApproved.setItem(item);
        bookingApproved.setBooker(booker);
        bookingRepository.save(bookingApproved);

        List<Booking> waitingBookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING);

        assertThat(waitingBookings).hasSize(1);
        assertThat(waitingBookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void testFindByItemOwnerIdAndStatusOrderByStartDesc() {
        User owner = new User();
        owner.setName("Owner Four");
        owner.setEmail("owner4@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Four");
        booker.setEmail("booker4@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Item Four");
        item.setDescription("Description Four");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Booking bookingWaiting = new Booking();
        bookingWaiting.setStart(LocalDateTime.now().minusDays(3));
        bookingWaiting.setEnd(LocalDateTime.now().minusDays(3).plusHours(2));
        bookingWaiting.setStatus(Status.WAITING);
        bookingWaiting.setItem(item);
        bookingWaiting.setBooker(booker);
        bookingRepository.save(bookingWaiting);

        Booking bookingApproved = new Booking();
        bookingApproved.setStart(LocalDateTime.now().minusDays(1));
        bookingApproved.setEnd(LocalDateTime.now().minusDays(1).plusHours(2));
        bookingApproved.setStatus(Status.APPROVED);
        bookingApproved.setItem(item);
        bookingApproved.setBooker(booker);
        bookingRepository.save(bookingApproved);

        List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void testFindByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        User booker = new User();
        booker.setName("Booker Five");
        booker.setEmail("booker5@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner Five");
        owner.setEmail("owner5@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item Five");
        item.setDescription("Description Five");
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
        bookingRepository.save(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(2).plusHours(1));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        List<Booking> result = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                booker.getId(), now, now
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStart()).isEqualTo(currentBooking.getStart());
    }

    @Test
    void testFindByBookerIdAndEndBeforeOrderByStartDesc() {
        User booker = new User();
        booker.setName("Booker Six");
        booker.setEmail("booker6@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner Six");
        owner.setEmail("owner6@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item Six");
        item.setDescription("Description Six");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = new Booking();
        booking1.setStart(now.minusDays(5));
        booking1.setEnd(now.minusDays(4));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setBooker(booker);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.minusDays(3));
        booking2.setEnd(now.minusDays(2));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(booker);
        bookingRepository.save(booking2);

        List<Booking> result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), now);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStart()).isAfterOrEqualTo(result.get(1).getStart());
    }

    @Test
    void testFindByBookerIdAndStartAfterOrderByStartDesc() {
        User booker = new User();
        booker.setName("Booker Seven");
        booker.setEmail("booker7@example.com");
        booker = userRepository.save(booker);

        User owner = new User();
        owner.setName("Owner Seven");
        owner.setEmail("owner7@example.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item Seven");
        item.setDescription("Description Seven");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking futureBooking1 = new Booking();
        futureBooking1.setStart(now.plusDays(1));
        futureBooking1.setEnd(now.plusDays(1).plusHours(2));
        futureBooking1.setStatus(Status.WAITING);
        futureBooking1.setItem(item);
        futureBooking1.setBooker(booker);
        futureBooking1 = bookingRepository.save(futureBooking1);

        Booking futureBooking2 = new Booking();
        futureBooking2.setStart(now.plusDays(2));
        futureBooking2.setEnd(now.plusDays(2).plusHours(2));
        futureBooking2.setStatus(Status.WAITING);
        futureBooking2.setItem(item);
        futureBooking2.setBooker(booker);
        futureBooking2 = bookingRepository.save(futureBooking2);

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(1));
        pastBooking.setEnd(now.minusDays(1).plusHours(2));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        List<Booking> result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), now);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStart()).isEqualTo(futureBooking2.getStart());
        assertThat(result.get(1).getStart()).isEqualTo(futureBooking1.getStart());
    }

    @Test
    void testFindByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        User owner = new User();
        owner.setName("Owner Active");
        owner.setEmail("owner_active@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Active");
        booker.setEmail("booker_active@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Active Item");
        item.setDescription("Item for active booking test");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking activeBooking = new Booking();
        activeBooking.setStart(now.minusHours(2));
        activeBooking.setEnd(now.plusHours(2));
        activeBooking.setStatus(Status.APPROVED);
        activeBooking.setItem(item);
        activeBooking.setBooker(booker);
        activeBooking = bookingRepository.save(activeBooking);

        Booking pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStart(now.plusHours(3));
        futureBooking.setEnd(now.plusHours(5));
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        bookingRepository.save(futureBooking);

        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                owner.getId(), now, now
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(activeBooking.getId());
    }

    @Test
    void testFindByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        User owner = new User();
        owner.setName("Owner Test");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Test");
        booker.setEmail("booker@test.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setStart(now.minusDays(4));
        booking1.setEnd(now.minusDays(3));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setBooker(booker);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.minusDays(3));
        booking2.setEnd(now.minusDays(2));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(booker);
        bookingRepository.save(booking2);

        Booking bookingFuture = new Booking();
        bookingFuture.setStart(now.plusDays(1));
        bookingFuture.setEnd(now.plusDays(2));
        bookingFuture.setStatus(Status.APPROVED);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(booker);
        bookingRepository.save(bookingFuture);

        List<Booking> result = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(owner.getId(), now);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStart()).isEqualTo(booking2.getStart());
        assertThat(result.get(1).getStart()).isEqualTo(booking1.getStart());
    }

    @Test
    void testFindByItemOwnerIdAndStartAfterOrderByStartDesc() {
        User owner = new User();
        owner.setName("Owner Future");
        owner.setEmail("owner.future@test.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Future");
        booker.setEmail("booker.future@test.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Future Item");
        item.setDescription("Future Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setStart(now.plusDays(1));
        booking1.setEnd(now.plusDays(1).plusHours(2));
        booking1.setStatus(Status.WAITING);
        booking1.setItem(item);
        booking1.setBooker(booker);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusDays(2));
        booking2.setEnd(now.plusDays(2).plusHours(2));
        booking2.setStatus(Status.WAITING);
        booking2.setItem(item);
        booking2.setBooker(booker);
        bookingRepository.save(booking2);

        Booking bookingPast = new Booking();
        bookingPast.setStart(now.minusDays(1));
        bookingPast.setEnd(now.minusDays(1).plusHours(2));
        bookingPast.setStatus(Status.APPROVED);
        bookingPast.setItem(item);
        bookingPast.setBooker(booker);
        bookingRepository.save(bookingPast);

        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(owner.getId(), now);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStart()).isEqualTo(booking2.getStart());
        assertThat(result.get(1).getStart()).isEqualTo(booking1.getStart());
    }

    @Test
    void testFindFirstByItemIdAndEndBeforeOrderByEndDesc() {
        // Arrange: создаём владельца, пользователя (booker) и вещь
        User owner = new User();
        owner.setName("Owner Optional");
        owner.setEmail("owner.optional@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Optional");
        booker.setEmail("booker.optional@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Optional Item");
        item.setDescription("Description Optional");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking booking1 = new Booking();
        booking1.setStart(now.minusDays(5));
        booking1.setEnd(now.minusDays(4));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(item);
        booking1.setBooker(booker);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.minusDays(3));
        booking2.setEnd(now.minusDays(2));
        booking2.setStatus(Status.APPROVED);
        booking2.setItem(item);
        booking2.setBooker(booker);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(now.minusDays(2));
        booking3.setEnd(now.minusDays(1));
        booking3.setStatus(Status.APPROVED);
        booking3.setItem(item);
        booking3.setBooker(booker);
        bookingRepository.save(booking3);

        Optional<Booking> result = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), now);

        assertThat(result).isPresent();
        assertThat(result.get().getEnd()).isEqualTo(booking3.getEnd());
    }

    @Test
    void testFindFirstByItemIdAndStartAfterOrderByStartAsc() {
        User owner = new User();
        owner.setName("Owner Optional2");
        owner.setEmail("owner.optional2@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Optional2");
        booker.setEmail("booker.optional2@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Optional Item 2");
        item.setDescription("Description Optional 2");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();


        Booking booking1 = new Booking();
        booking1.setStart(now.plusDays(2));
        booking1.setEnd(now.plusDays(2).plusHours(2));
        booking1.setStatus(Status.WAITING);
        booking1.setItem(item);
        booking1.setBooker(booker);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStart(now.plusDays(1));
        booking2.setEnd(now.plusDays(1).plusHours(2));
        booking2.setStatus(Status.WAITING);
        booking2.setItem(item);
        booking2.setBooker(booker);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setStart(now.plusDays(3));
        booking3.setEnd(now.plusDays(3).plusHours(2));
        booking3.setStatus(Status.WAITING);
        booking3.setItem(item);
        booking3.setBooker(booker);
        bookingRepository.save(booking3);

        Optional<Booking> result = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), now);

        assertThat(result).isPresent();
        assertThat(result.get().getStart()).isEqualTo(booking2.getStart());
    }

    @Test
    void testFindByBookerIdAndItemIdAndEndBefore() {
        User owner = new User();
        owner.setName("Owner Test");
        owner.setEmail("owner.test@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker Test");
        booker.setEmail("booker.test@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setStart(now.minusDays(2));
        booking.setEnd(now.minusDays(1));
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);
        booking.setBooker(booker);
        bookingRepository.save(booking);

        Optional<Booking> result = bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                booker.getId(), item.getId(), now
        );

        assertThat(result).isPresent();
        assertThat(result.get().getEnd()).isEqualTo(booking.getEnd());
    }
}