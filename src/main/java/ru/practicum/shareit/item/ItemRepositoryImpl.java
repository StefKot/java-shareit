package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> storage = new HashMap<>();
    private long lastId = 1;

    @Override
    public List<Item> findByUserId(Long userId) {
        return storage.values().stream()
                .filter(item -> Objects.equals(item.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item findByItemId(Long itemId) {
        return storage.get(itemId);
    }

    @Override
    public Item update(Long userId, Long itemId, Item itemUpdates) {
        Item existingItem = storage.get(itemId);

        if (existingItem == null || !Objects.equals(existingItem.getUserId(), userId)) {
            return null;
        }

        if (itemUpdates.getName() != null) {
            existingItem.setName(itemUpdates.getName());
        }
        if (itemUpdates.getDescription() != null) {
            existingItem.setDescription(itemUpdates.getDescription());
        }
        if (itemUpdates.getAvailable() != null) {
            existingItem.setAvailable(itemUpdates.getAvailable());
        }
        if (itemUpdates.getOwner() != null) {
            existingItem.setOwner(itemUpdates.getOwner());
        }
        if (itemUpdates.getRequest() != null) {
            existingItem.setRequest(itemUpdates.getRequest());
        }

        return existingItem;
    }

    @Override
    public List<Item> findForNameOrDesc(String text) {
        List<Item> result = new ArrayList<>();
        String lowerCaseText = text.toLowerCase();
        for (Item item : storage.values()) {
            if (Boolean.TRUE.equals(item.getAvailable())
                    && (item.getName().toLowerCase().contains(lowerCaseText)
                    || item.getDescription().toLowerCase().contains(lowerCaseText))) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        Item item = storage.get(itemId);
        if (item != null && Objects.equals(item.getUserId(), userId)) {
            storage.remove(itemId);
        }
    }

    private long getId() {
        return lastId++;
    }
}