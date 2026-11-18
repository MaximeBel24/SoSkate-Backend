package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.ServiceEntity;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.exceptions.booking.SlotNotAvailableException;
import com.soskate.api.mappers.BookingMapper;
import com.soskate.api.repositories.AvailabilityBlockRepository;
import com.soskate.api.repositories.BookingParticipantRepository;
import com.soskate.api.repositories.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    private static final LocalDateTime START = LocalDateTime.of(2027, 3, 10, 14, 0);
    private static final BookingCreateRequest ONE_HOUR =
            new BookingCreateRequest(1L, 2L, 3L, 4L, START, 60, 1, null);

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final AvailabilityBlockRepository blockRepository = mock(AvailabilityBlockRepository.class);
    private final BookingContextLoader contextLoader = mock(BookingContextLoader.class);
    private final BookingServiceImpl service = new BookingServiceImpl(
            bookingRepository,
            mock(BookingParticipantRepository.class),
            blockRepository,
            contextLoader,
            mock(BookingValidationService.class),
            mock(BookingMapper.class)
    );

    @BeforeEach
    void setUp() {
        ServiceEntity lessonType = ServiceEntity.builder().basePriceCents(4000).build();
        BookingContext context = new BookingContext(
                new CustomerEntity(),
                new InstructorEntity(),
                new SpotEntity(),
                lessonType
        );
        when(contextLoader.loadForCreation(ONE_HOUR)).thenReturn(context);
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void aBookingIsUndoneIfOneOfItsBlocksIsGone() {
        when(blockRepository.takeBlocks(any(), any(), any())).thenReturn(3);

        assertThatThrownBy(() -> service.createBooking(ONE_HOUR)).isInstanceOf(SlotNotAvailableException.class);
    }

    @Test
    void aBookingGoesThroughOnceItHasAllItsBlocks() {
        when(blockRepository.takeBlocks(any(), any(), any())).thenReturn(4);

        assertThatCode(() -> service.createBooking(ONE_HOUR)).doesNotThrowAnyException();
    }
}
