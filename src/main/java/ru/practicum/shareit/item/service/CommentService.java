package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

public interface CommentService {

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

}