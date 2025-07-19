package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingInfoService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingInfoService bookingInfoService;
    private final CommentInfoService commentInfoService;

    @Override
    @Transactional
    public Item create(Item item, Long ownerId) {
        validateItem(item);
        User owner = userService.getById(ownerId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Item item, Long ownerId) {
        Item existingItem = getById(item.getId());

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Редактировать вещь может только владелец");
        }

        updateItemFields(existingItem, item);
        return itemRepository.save(existingItem);
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + itemId + " не найдена"));
    }

    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        Item item = getById(itemId);
        ItemDto itemDto = ItemMapper.toDto(item);

        if (item.getOwner().getId().equals(userId)) {
            setBookingInfo(itemDto, itemId);
        }

        itemDto.setComments(commentInfoService.getCommentsByItemId(itemId));
        return itemDto;
    }


    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        userService.getById(ownerId);
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<CommentDto>> commentsMap = commentInfoService.getCommentsByItemIds(itemIds);
        Map<Long, BookingShortDto> lastBookingsMap = bookingInfoService.findLastBookingsForItems(itemIds);
        Map<Long, BookingShortDto> nextBookingsMap = bookingInfoService.findNextBookingsForItems(itemIds);

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toDto(item);
                    dto.setComments(commentsMap.getOrDefault(item.getId(), Collections.emptyList()));
                    dto.setLastBooking(lastBookingsMap.get(item.getId()));
                    dto.setNextBooking(nextBookingsMap.get(item.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAvailableItemsByText(text.toLowerCase()).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long itemId, Long ownerId) {
        Item item = getById(itemId);
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Удалять вещь может только владелец");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> findAllByRequestId(Long requestId) {
        return itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findAllByRequestIdIn(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void setBookingInfo(ItemDto dto, Long itemId) {
        dto.setLastBooking(bookingInfoService.getLastBooking(itemId));
        dto.setNextBooking(bookingInfoService.getNextBooking(itemId));
    }

    private void updateItemFields(Item existingItem, Item newItem) {
        if (newItem.getName() != null) {
            existingItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            existingItem.setAvailable(newItem.getAvailable());
        }
        if (newItem.getRequestId() != null) {
            existingItem.setRequestId(newItem.getRequestId());
        }
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан");
        }
    }
}