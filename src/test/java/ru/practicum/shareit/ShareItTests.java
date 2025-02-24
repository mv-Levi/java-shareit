package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@SpringBootTest
class ShareItTests {
	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testSaveBooking() {
		Booking booking = new Booking();
		booking.setStart(LocalDateTime.now());
		booking.setEnd(LocalDateTime.now().plusDays(1));
		booking.setItem(itemRepository.findById(1L).orElseThrow(() -> new RuntimeException("Item not found")));
		booking.setBooker(userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found")));
		booking.setStatus(Status.WAITING);
		bookingRepository.save(booking);
	}
	@Test
	void contextLoads() {
	}


}
