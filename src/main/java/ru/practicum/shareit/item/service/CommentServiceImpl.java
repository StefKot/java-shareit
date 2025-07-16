package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingInfoService;
import ru.practicum.shareit.exception.CommentException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemInfoService itemInfoService;
    private final BookingInfoService bookingInfoService;

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        validateComment(commentDto);
        User author = userService.getById(userId);
        Item item = itemInfoService.getById(itemId);

        Optional<Booking> lastBooking = bookingInfoService.findLastUserBookingForItem(userId, itemId);
        if (lastBooking.isEmpty() || lastBooking.get().getEnd().isAfter(LocalDateTime.now())) {
            throw new CommentException("Пользователь не бронировал эту вещь или бронирование еще не завершено");
        }

        validateUserBookedItem(userId, itemId);

        Comment comment = buildComment(commentDto, author, item);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toDto(savedComment);
    }

    private void validateComment(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().trim().isEmpty()) {
            throw new CommentException("Текст комментария не может быть пустым");
        }
    }

    private void validateUserBookedItem(Long userId, Long itemId) {
        if (!bookingInfoService.hasUserBookedItem(userId, itemId)) {
            throw new CommentException("Пользователь не бронировал эту вещь");
        }
    }

    private Comment buildComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }
}