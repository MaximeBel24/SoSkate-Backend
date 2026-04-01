package com.soskate.api.services.admin;

import com.soskate.api.dto.admin.DashboardResponse;
import com.soskate.api.dto.admin.RecentBooking;
import com.soskate.api.dto.admin.RecentCustomer;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.repositories.BookingRepository;
import com.soskate.api.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public DashboardResponse getDashboardData() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        Long monthlyCustomersCreated = customerRepository.countByCreatedAtBetween(monthStart, tomorrowStart);

        Long dailyBookings = bookingRepository.countByCreatedAtBetween(todayStart, tomorrowStart);
        Long monthlyBookings = bookingRepository.countByCreatedAtBetween(monthStart, tomorrowStart);

        List<CustomerEntity> lastTenCustomersCreated = customerRepository.findTop10ByOrderByCreatedAtDesc();

        List<BookingEntity> lastTenBookings = bookingRepository.findTop10ByOrderByCreatedAtDesc();

        List<RecentBooking> recentBookings = lastTenBookings.stream()
                .map(b -> new RecentBooking(
                        b.getId(),
                        b.getStartTime(),
                        b.getService() != null ? b.getService().getName() : "N/A",
                        b.getInstructor() != null ? b.getInstructor().getFirstName() : "N/A",
                        b.getInstructor() != null ? b.getInstructor().getLastName() : "",
                        b.getStatus()
                ))
                .toList();


        List<RecentCustomer> recentCustomers = lastTenCustomersCreated.stream()
                .map(c -> new RecentCustomer(
                        c.getId(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getEmail(),
                        c.getCreatedAt()
                ))
                .toList();

        return new DashboardResponse(
                dailyBookings,
                monthlyBookings,
                monthlyCustomersCreated,
                recentBookings,
                recentCustomers
        );

    }
}