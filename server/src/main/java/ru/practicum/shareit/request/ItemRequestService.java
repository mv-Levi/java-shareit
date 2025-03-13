package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Long requestorId, ItemRequestDto requestDto);

    List<ItemRequestDto> getUserRequests(Long requestorId);

    List<ItemRequestDto> getAllRequests(Long requestorId);

    ItemRequestDto getRequestById(Long requestorId, Long requestId);

}
