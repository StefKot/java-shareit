package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;
    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto create(
            @RequestBody ItemRequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        ItemRequest request = ItemRequestMapper.toEntity(requestDto, null);
        ItemRequest createdRequest = requestService.create(request, requesterId);
        return ItemRequestMapper.toDto(createdRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequest request = requestService.getById(requestId, userId);
        List<ItemDto> items = itemService.findAllByRequestId(requestId);
        return ItemRequestMapper.toWithItemsDto(request, items);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getAllByRequester(
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        List<ItemRequest> requests = requestService.getAllByRequester(requesterId);
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<ItemDto> allItems = itemService.findAllByRequestIdIn(requestIds);
        Map<Long, List<ItemDto>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> ItemRequestMapper.toWithItemsDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList()))
                )
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getAllExceptRequester(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return requestService.getAllExceptRequesterWithItems(requesterId, from, size);
    }

    @DeleteMapping("/{requestId}")
    public void delete(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        requestService.delete(requestId, userId);
    }
}