package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;


    public Item addNewItem(Long userId, Item item) {
        if (userService.getAllUsers().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new UserNotFoundException("User not found");
        }
        if (item.getAvailable() == null || item.getDescription() == null || item.getDescription().isBlank() || item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("Available is null");
        }
        item.setUserId(userId);
        return itemRepository.save(item);
    }

    public List<Item> getItems(Long userId) {
        return itemRepository.findByUserId(userId);
    }

    public List<Item> findItemForNameOrDescription(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findForNameOrDesc(text);
    }

    public Item updateItem(Long userId, Long itemId, Item item) {
        if (userService.getAllUsers().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new UserNotFoundException("User not found");
        }
        if (userId <= 0 || itemId < 0) {
            throw new IllegalArgumentException("Invalid userId or itemId");
        }
        return itemRepository.update(userId, itemId, item);
    }

    public Item getItem(Long itemId) {
        return itemRepository.findByItemId(itemId);
    }

    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

}