package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        userService.getUserById(userId);
        validateNewItem(itemDto);

        Item item = ItemMapper.toItem(itemDto);
        item.setUserId(userId);

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        userService.getUserById(userId);

        Item existingItem = findItemByIdOrThrow(itemId);

        if (!Objects.equals(existingItem.getUserId(), userId)) {
            throw new ItemNotFoundException("User " + userId + " is not the owner of item " + itemId);
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = findItemByIdOrThrow(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        userService.getUserById(userId);
        return itemRepository.findByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemForNameOrDescription(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findForNameOrDesc(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        userService.getUserById(userId);
        Item item = findItemByIdOrThrow(itemId);
        if (!Objects.equals(item.getUserId(), userId)) {
            throw new ItemNotFoundException("User " + userId + " is not the owner of item " + itemId);
        }
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    private Item findItemByIdOrThrow(Long itemId) {
        Item item = itemRepository.findByItemId(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Item with id " + itemId + " not found.");
        }
        return item;
    }

    private void validateNewItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Item description cannot be null or empty.");
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Item availability status cannot be null.");
        }
    }
}