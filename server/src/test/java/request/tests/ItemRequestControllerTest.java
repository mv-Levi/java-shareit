package request.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = ru.practicum.shareit.ShareItServer.class)
@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(10L);
        requestDto.setDescription("Need a laptop");
        requestDto.setCreated(LocalDateTime.of(2025, 3, 1, 12, 0));

        Mockito.when(itemRequestService.createRequest(Mockito.eq(userId), Mockito.any(ItemRequestDto.class)))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.description").value("Need a laptop"))
                .andExpect(jsonPath("$.created").value("2025-03-01T12:00:00"));
    }

    @Test
    void testGetUserRequests() throws Exception {
        Long userId = 2L;
        ItemRequestDto req1 = new ItemRequestDto();
        req1.setId(201L);
        req1.setDescription("Other user request 1");
        req1.setCreated(LocalDateTime.of(2025, 5, 1, 9, 0));
        req1.setItems(null);

        ItemRequestDto req2 = new ItemRequestDto();
        req2.setId(202L);
        req2.setDescription("Other user request 2");
        req2.setCreated(LocalDateTime.of(2025, 5, 2, 9, 0));
        req2.setItems(null);

        Mockito.when(itemRequestService.getUserRequests(userId))
                .thenReturn(List.of(req2, req1));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(202))
                .andExpect(jsonPath("$[1].id").value(201));
    }

    @Test
    void testGetAllRequests() throws Exception {
        Long userId = 3L;

        ItemRequestDto req1 = new ItemRequestDto();
        req1.setId(201L);
        req1.setDescription("Other user request 1");
        req1.setCreated(LocalDateTime.of(2025, 5, 1, 9, 0));
        req1.setItems(null);

        ItemRequestDto req2 = new ItemRequestDto();
        req2.setId(202L);
        req2.setDescription("Other user request 2");
        req2.setCreated(LocalDateTime.of(2025, 5, 2, 9, 0));
        req2.setItems(null);

        Mockito.when(itemRequestService.getAllRequests(userId))
                .thenReturn(List.of(req1, req2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(201))
                .andExpect(jsonPath("$[1].id").value(202));
    }

    @Test
    void testGetRequestById() throws Exception {
        Long userId = 4L;
        Long requestId = 301L;

        ItemRequestDto req = new ItemRequestDto();
        req.setId(requestId);
        req.setDescription("Single request");
        req.setCreated(LocalDateTime.of(2025, 6, 10, 8, 0));
        req.setItems(null);

        Mockito.when(itemRequestService.getRequestById(userId, requestId))
                .thenReturn(req);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(301))
                .andExpect(jsonPath("$.description").value("Single request"))
                .andExpect(jsonPath("$.created").value("2025-06-10T08:00:00"));
    }
}
