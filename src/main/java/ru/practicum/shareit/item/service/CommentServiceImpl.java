package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemInfoService itemInfoService;

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User author = userService.getById(userId);
        Item item = itemInfoService.getById(itemId);

        List<Booking> userBookings = bookingRepository.findPastBookingsForComment(userId, itemId, LocalDateTime.now());
        if (userBookings.isEmpty()) {
            throw new ValidationException("Оставлять комментарии могут только пользователи, которые завершили бронирование данной вещи.");
        }

        if (commentRepository.existsByAuthorIdAndItemId(userId, itemId)) {
            throw new ValidationException("Вы уже оставляли комментарий к этой вещи.");
        }

        Comment comment = CommentMapper.toEntity(commentDto, author, item);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toDto(savedComment);
    }
}