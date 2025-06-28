package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, List<Item>> storage = new HashMap<>();
    private long lastId = 1;

    @Override
    public List<Item> findByUserId(Long userId) {
        return storage.get(userId);
    }

    @Override
    public Item findByItemId(Long itemId) {
        for (List<Item> items : storage.values()) {
            for (Item item : items) {
                if (Objects.equals(item.getId(), itemId)) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        for (Item i : storage.get(userId)) {
            int index = storage.get(userId).indexOf(i);
            if (Objects.equals(userId, i.getUserId()) && Objects.equals(itemId, i.getId())) {
                Item updated = storage.get(userId).get(index);
                updated.setName(item.getName() == null ? updated.getName() : item.getName());
                updated.setDescription(item.getDescription() == null ? updated.getDescription() : item.getDescription());
                updated.setAvailable(item.getAvailable() == null ? updated.getAvailable() : item.getAvailable());
                updated.setOwner(item.getOwner() == null ? updated.getOwner() : item.getOwner());
                updated.setRequest(item.getRequest() == null ? updated.getRequest() : item.getRequest());
                storage.get(userId).set(index, updated);
                return updated;
            }
        }
        return null;
    }

    @Override
    public List<Item> findForNameOrDesc(String text) {
        List<Item> result = new ArrayList<>();
        for (List<Item> items : storage.values()) {
            for (Item item : items) {
                if (Boolean.TRUE.equals(item.getAvailable())
                        && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public Item save(Item item) {
        if (storage.containsKey(item.getUserId())) {
            item.setId(getId());
            storage.get(item.getUserId()).add(item);
            return item;
        } else {
            item.setId(getId());
            ArrayList<Item> items = new ArrayList<>();
            items.add(item);
            storage.put(item.getUserId(), items);
            return item;
        }
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        storage.get(userId).removeIf(item -> Objects.equals(item.getId(), itemId));
    }

    private long getId() {
        return lastId++;
    }
}