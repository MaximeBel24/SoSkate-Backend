package com.soskate.api.services.admin;

import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.dto.booking.UpdateNotesRequest;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.enums.BookingStatus;
import com.soskate.api.exceptions.booking.BookingNotFoundException;
import com.soskate.api.exceptions.booking.CancellationNotAllowedException;
import com.soskate.api.mappers.BookingMapper;
import com.soskate.api.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminBookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public List<BookingResponse> getAllBookings() {
        log.debug("Retrieving all bookings");
        List<BookingEntity> bookings = bookingRepository.findAllByOrderByCreatedAtDesc();
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        log.info("Cancelling booking {} by admin", bookingId);
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for id: " + bookingId));

        if(booking.isCancelled() || booking.isCompleted()) {
            throw new CancellationNotAllowedException();
        }

        booking.setStatus(BookingStatus.CANCELLED);
        BookingEntity savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);

    }

    public BookingResponse getBookingById(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for id: " + bookingId));
        return bookingMapper.toResponse(booking);
    }

    @Transactional
    public BookingResponse updateNotes(Long bookingId, UpdateNotesRequest request) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for id: " + bookingId));
        booking.setNotes(request.notes());
        bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    @Transactional
    public BookingResponse updateStatus(Long bookingId, BookingStatus newStatus) {
        log.info("Updating booking {} status to {} by admin", bookingId, newStatus);
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found for id: " + bookingId));

        if (booking.getStatus() == newStatus) {
            throw new IllegalStateException("Le booking est déjà au statut " + newStatus);
        }

        if (booking.isCancelled() || booking.isCompleted()) {
            throw new IllegalStateException("Impossible de modifier le statut d'un booking " + booking.getStatus().getDisplayName());
        }

        if (booking.isConfirmed() && newStatus != BookingStatus.CANCELLED) {
            throw new IllegalStateException("Un booking confirmé ne peut qu'être annulé");
        }

        booking.setStatus(newStatus);
        BookingEntity savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }


}
