package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@ActiveProfiles("test")
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
		// Создаем тестового пользователя
		User user = new User();
		user.setName("Test User");
		user.setEmail("test@example.com");
		user = userRepository.save(user);

		// Создаем тестовый предмет и устанавливаем его владельцем созданного пользователя
		Item item = new Item();
		item.setName("Test Item");
		item.setDescription("Test Description");
		item.setAvailable(true);
		item.setOwner(user);
		item = itemRepository.save(item);

		// Теперь создаем бронирование, используя сохраненные данные
		Booking booking = new Booking();
		booking.setStart(LocalDateTime.now());
		booking.setEnd(LocalDateTime.now().plusDays(1));
		booking.setItem(item);
		booking.setBooker(user);
		booking.setStatus(Status.WAITING);
		booking = bookingRepository.save(booking);

		// Дополнительно можно добавить проверки:
		assertNotNull(booking.getId());
	}

	@Test
	void contextLoads() {
	}

}
