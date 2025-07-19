package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingInfoServiceImpl implements BookingInfoService {
    private final BookingRepository bookingRepository;

    @Override
    public BookingShortDto getLastBooking(Long itemId) {
        List<Booking> lastBookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(
                itemId, LocalDateTime.now());

        return lastBookings.stream()
                .findFirst()
                .map(this::convertToShortDto)
                .orElse(null);
    }

    @Override
    public BookingShortDto getNextBooking(Long itemId) {
        List<Booking> nextBookings = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(
                itemId, LocalDateTime.now());

        return nextBookings.stream()
                .findFirst()
                .map(this::convertToShortDto)
                .orElse(null);
    }

    @Override
    public boolean hasUserBookedItem(Long userId, Long itemId) {
        return bookingRepository.hasUserFinishedBooking(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
    }

    private BookingShortDto convertToShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    @Override
    public Optional<BookingShortDto> findLastUserBookingForItem(Long userId, Long itemId) {
        return bookingRepository.findFirstByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(
                        userId, itemId, LocalDateTime.now())
                .map(this::convertToShortDto);
    }

    @Override
    public boolean isItemAvailableForDates(Long itemId, LocalDateTime start, LocalDateTime end) {
        return !bookingRepository.existsApprovedBookingsForItemBetweenDates(
                itemId,
                BookingStatus.APPROVED,
                start,
                end);
    }

    @Override
    public Map<Long, BookingShortDto> findLastBookingsForItems(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Booking> bookings = bookingRepository.findLastBookingsForItems(itemIds, LocalDateTime.now());

        return bookings.stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        this::convertToShortDto,
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public Map<Long, BookingShortDto> findNextBookingsForItems(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Booking> bookings = bookingRepository.findNextBookingsForItems(itemIds, LocalDateTime.now());

        return bookings.stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        this::convertToShortDto,
                        (existing, replacement) -> existing
                ));
    }
}