package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingInfoService {

    BookingShortDto getLastBooking(Long itemId);

    BookingShortDto getNextBooking(Long itemId);

    boolean hasUserBookedItem(Long userId, Long itemId);

    Optional<Booking> findLastUserBookingForItem(Long userId, Long itemId);

    boolean isItemAvailableForDates(Long itemId, LocalDateTime start, LocalDateTime end);

    Map<Long, BookingShortDto> findLastBookingsForItems(List<Long> itemIds);

    Map<Long, BookingShortDto> findNextBookingsForItems(List<Long> itemIds);
}