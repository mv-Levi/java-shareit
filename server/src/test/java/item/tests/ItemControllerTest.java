package item.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddItem() throws Exception {
        // Arrange
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);
        // Дополнительные поля (например, ownerId, requestId) могут оставаться null
        Long userId = 10L;

        Mockito.when(itemService.addItem(Mockito.eq(userId), Mockito.any(ItemDto.class)))
                .thenReturn(itemDto);

        // Act & Assert
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testUpdateItem() throws Exception {
        // Arrange
        Long userId = 20L;
        Long itemId = 2L;
        ItemDto updateDto = new ItemDto();
        updateDto.setId(itemId);
        updateDto.setName("Updated Item");
        updateDto.setDescription("Updated description");
        updateDto.setAvailable(false);

        Mockito.when(itemService.updateItem(Mockito.eq(itemId), Mockito.eq(userId), Mockito.any(ItemDto.class)))
                .thenReturn(updateDto);

        // Act & Assert
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testGetItem() throws Exception {
        // Arrange
        Long itemId = 3L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Fetched Item");
        itemDto.setDescription("Fetched description");
        itemDto.setAvailable(true);

        Mockito.when(itemService.getItem(Mockito.eq(itemId))).thenReturn(itemDto);

        // Act & Assert
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Fetched Item"))
                .andExpect(jsonPath("$.description").value("Fetched description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testGetItems() throws Exception {
        // Arrange
        Long userId = 30L;
        ItemDto item1 = new ItemDto();
        item1.setId(4L);
        item1.setName("Item One");
        item1.setDescription("First item");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setId(5L);
        item2.setName("Item Two");
        item2.setDescription("Second item");
        item2.setAvailable(true);

        Mockito.when(itemService.getItemsByOwner(Mockito.eq(userId)))
                .thenReturn(List.of(item1, item2));

        // Act & Assert
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[1].id").value(5));
    }

    @Test
    void testSearchItems() throws Exception {
        // Arrange
        // Создаем два результата поиска
        ItemDto item1 = new ItemDto();
        item1.setId(6L);
        item1.setName("Laptop Pro");
        item1.setDescription("High performance laptop");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setId(7L);
        item2.setName("Notebook");
        item2.setDescription("This is a laptop computer");
        item2.setAvailable(true);

        Mockito.when(itemService.searchItems(Mockito.eq("laptop")))
                .thenReturn(List.of(item1, item2));

        // Act & Assert
        mockMvc.perform(get("/items/search")
                        .param("text", "laptop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro"))
                .andExpect(jsonPath("$[1].name").value("Notebook"));
    }

    @Test
    void testAddComment() throws Exception {
        // Arrange
        Long userId = 40L;
        Long itemId = 8L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(10L);
        commentDto.setText("Great item!");
        // Допустим, комментарий также содержит дату создания и имя автора, установленные сервисом
        commentDto.setAuthorName("Commenter");
        // Для простоты не задаем created в DTO, сервис устанавливает его автоматически

        Mockito.when(itemService.addComment(Mockito.eq(userId), Mockito.eq(itemId), Mockito.any(CommentDto.class)))
                .thenReturn(commentDto);

        // Act & Assert
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("Commenter"));
    }
}
