package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;
import java.util.Map;

public interface CommentInfoService {

    List<CommentDto> getCommentsByItemId(Long itemId);

    Map<Long, List<CommentDto>> getCommentsByItemIds(List<Long> itemIds);
}