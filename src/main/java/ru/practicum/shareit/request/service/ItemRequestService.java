package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest create(ItemRequest request, Long requesterId);

    ItemRequest getById(Long requestId, Long userId);

    List<ItemRequest> getAllByRequester(Long requesterId);

    List<ItemRequestWithItemsDto> getAllByRequesterWithItems(Long requesterId);

    List<ItemRequestWithItemsDto> getAllExceptRequesterWithItems(Long requesterId, int from, int size);

    List<ItemRequest> getAllExceptRequester(Long requesterId, int from, int size);

    void delete(Long requestId, Long userId);
}