package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingInfoService {
    private final BookingRepository bookingRepository;

    public BookingShortDto getLastBooking(Long itemId) {
        List<Booking> lastBookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(
                itemId, LocalDateTime.now());

        return lastBookings.stream()
                .findFirst()
                .map(this::convertToShortDto)
                .orElse(null);
    }

    public BookingShortDto getNextBooking(Long itemId) {
        List<Booking> nextBookings = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(
                itemId, LocalDateTime.now());

        return nextBookings.stream()
                .findFirst()
                .map(this::convertToShortDto)
                .orElse(null);
    }

    public boolean hasUserBookedItem(Long userId, Long itemId) {
        return bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
    }

    private BookingShortDto convertToShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    // Дополнительные методы при необходимости
    public Optional<Booking> findLastUserBookingForItem(Long userId, Long itemId) {
        return bookingRepository.findFirstByBookerIdAndItemIdAndEndBeforeOrderByEndDesc(
                userId, itemId, LocalDateTime.now());
    }

    public boolean isItemAvailableForDates(Long itemId, LocalDateTime start, LocalDateTime end) {
        return !bookingRepository.existsApprovedBookingsForItemBetweenDates(
                itemId,
                BookingStatus.APPROVED,
                start,
                end);
    }
}