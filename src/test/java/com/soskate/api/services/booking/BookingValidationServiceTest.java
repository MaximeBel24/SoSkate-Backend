package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.repositories.AvailabilityRepository;
import com.soskate.api.repositories.BookingRepository;
import com.soskate.api.repositories.InstructorSpotRepository;
import com.soskate.api.services.availability.BufferCalculationService;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@ExtendWith(MockitoExtension.class)
public class BookingValidationServiceTest {

    @Mock
    private InstructorSpotRepository instructorSpotRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BufferCalculationService bufferCalculationService;

    @InjectMocks
    private BookingValidationService bookingValidationService;

    @Test
    void validateDuration_withValidDuration_doesNotThrow() {
        assertThatNoException().isThrownBy(() -> bookingValidationService.validateDuration(60));
    }


    @Test
    void validateDuration_withInvalidDuration_throwsBookingException() {
        assertThatThrownBy(() -> bookingValidationService.validateDuration(50))
                .isInstanceOf(BookingException.class);
    }

    @Test
    void validateDuration_withNull_throwsBookingException() {
        assertThatThrownBy(() -> bookingValidationService.validateDuration(null))
                .isInstanceOf(BookingException.class);
    }

    @Test
    void validateForCreation_withInactiveInstructor_throwsBookingException() {
        InstructorEntity suspendedInstructor = InstructorEntity.builder()
                .firstName("Rodney")
                .lastName("Mullen")
                .email("rodney.mullen@skate.com")
                .password("wrongPassword")
                .status(InstructorStatus.SUSPENDED)
                .build();
        suspendedInstructor.setId(3L);

        SpotEntity spot = SpotEntity.builder().id(1L).build();

        BookingContext context = new BookingContext(null, suspendedInstructor, spot, null, null);
        BookingCreateRequest request = new BookingCreateRequest(null, null, null, null, null, 60, null, null);

        assertThatThrownBy(() -> bookingValidationService.validateForCreation(context, request))
                .isInstanceOf(BookingException.class);
    }

    @Test
    void validateForCreation_withInstructorNotAtSpot_throwsInstructorNotAtSpotException() {

    }

    @Test
    void validateForCreation_withBookingTooSoon_throwsBookingTooSoonException() {

    }

    @Test
    void validateForCreation_withNoAvailability_throwsInstructorNotAvailableException() {

    }

    @Test
    void validateForCreation_withConflictingBooking_throwsSlotNotAvailableException() {

    }
    @Test
    void validateForCreation_withTooManyParticipants_throwsBookingException() {

    }

    @Test
    void validateForCreation_withAllValid_doesNotThrow() {

    }
}
