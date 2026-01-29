package com.soskate.api.scheduler;

import com.soskate.api.entities.BookingEntity;
import com.soskate.api.enums.BookingStatus;
import com.soskate.api.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingStatusScheduler {

    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void markPassedBookAsCompleted() {
        log.info("Début du job de mise à jour des bookings");

        List<BookingEntity> passedBookings = bookingRepository
                .findPassedAndNotCompleted(LocalDateTime.now());

        log.info("{} bookings à mettre à jour", passedBookings.size());

        for (BookingEntity booking : passedBookings) {
            booking.setStatus(BookingStatus.COMPLETED);
        }

        bookingRepository.saveAll(passedBookings);
        log.info("Job terminé avec succès");
    }
}
