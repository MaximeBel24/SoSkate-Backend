package com.soskate.api.dto.admin;

import java.util.List;

public record DashboardResponse(

        // KPIs
        Long todayBookings,
        Long monthBookings,
        Long newCustomersThisMonth,

        // Derniers bookings
        List<RecentBooking> recentBookings,

        // Derniers inscrits
        List<RecentCustomer> recentCustomers
) {
}
