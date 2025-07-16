package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingInfoService;
import ru.practicum.shareit.exception.NotFoundException;
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
            throw new NotFoundException("Редактировать вещь может только владелец");
        }

        updateItemFields(existingItem, item);
        return itemRepository.save(existingItem);
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
    }

    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        Item item = getById(itemId);
        ItemDto itemDto = ItemMapper.toDto(item);

        if (userId != null && item.getOwner().getId().equals(userId)) {
            setBookingInfo(itemDto, itemId);
        }

        itemDto.setComments(commentInfoService.getCommentsByItemId(itemId));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        Map<Long, List<CommentDto>> comments = commentInfoService.getCommentsByItemIds(
                items.stream().map(Item::getId).collect(Collectors.toList())
        );

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toDto(item);
                    setBookingInfo(dto, item.getId());
                    dto.setComments(comments.getOrDefault(item.getId(), Collections.emptyList()));
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
            throw new NotFoundException("Удалять вещь может только владелец");
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
        if (requestIds.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
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

    private void setBookingInfo(ItemDto dto, Long itemId) {
        dto.setLastBooking(bookingInfoService.getLastBooking(itemId));
        dto.setNextBooking(bookingInfoService.getNextBooking(itemId));
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
        if (item.getName().length() > 255) {
            throw new ValidationException("Название вещи слишком длинное");
        }
        if (item.getDescription().length() > 1000) {
            throw new ValidationException("Описание вещи слишком длинное");
        }
    }
}