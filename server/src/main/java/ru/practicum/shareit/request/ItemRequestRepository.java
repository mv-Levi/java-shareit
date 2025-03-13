package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    // Запросы, созданные пользователем, отсортированные от новых к старым
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    // Запросы, созданные другими пользователями
    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId);

}
