package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.entities.*;
import com.soskate.api.exceptions.customer.CustomerNotFoundException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.service.ServiceNotFoundException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import com.soskate.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Loads all entities required for booking operations.
 * Centralizes entity fetching and exception handling.
 */
@Component
@RequiredArgsConstructor
public class BookingContextLoader {

    private final CustomerRepository customerRepository;
    private final InstructorRepository instructorRepository;
    private final SpotRepository spotRepository;
    private final ServiceRepository serviceRepository;

    /**
     * Locks the instructor before any other read, so that two bookings with one instructor run one
     * after the other: MySQL freezes what a transaction sees at its first read, so a read before the
     * lock would miss the lesson the previous booking has just written.
     */
    public BookingContext loadForCreation(BookingCreateRequest request) {
        InstructorEntity instructor = instructorRepository.findByIdForUpdate(request.instructorId())
                .orElseThrow(() -> new InstructorNotFoundException(request.instructorId()));

        CustomerEntity customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.customerId()));

        SpotEntity spot = spotRepository.findById(request.spotId())
                .orElseThrow(() -> new SpotNotFoundException(request.spotId()));

        ServiceEntity service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new ServiceNotFoundException(request.serviceId()));

        return new BookingContext(customer, instructor, spot, service);
    }
}
