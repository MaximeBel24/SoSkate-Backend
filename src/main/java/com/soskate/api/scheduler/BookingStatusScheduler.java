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
        log.info("Starting booking status update job");

        List<BookingEntity> passedBookings = bookingRepository
                .findPassedAndNotCompleted(LocalDateTime.now());

        log.info("{} bookings to update", passedBookings.size());

        for (BookingEntity booking : passedBookings) {
            booking.setStatus(BookingStatus.COMPLETED);
        }

        bookingRepository.saveAll(passedBookings);
        log.info("Job completed successfully");
    }
}
