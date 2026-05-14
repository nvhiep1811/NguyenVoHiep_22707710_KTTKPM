package com.kttkpm.bookingservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kttkpm.bookingservice.domain.Booking;
import com.kttkpm.bookingservice.domain.BookingStatus;
import com.kttkpm.bookingservice.dto.CreateBookingRequest;
import com.kttkpm.bookingservice.repository.BookingRepository;
import com.kttkpm.bookingservice.service.BookingEventPublisher;
import com.kttkpm.bookingservice.service.BookingService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BookingServiceTest {

    private final BookingRepository bookingRepository = org.mockito.Mockito.mock(BookingRepository.class);
    private final BookingEventPublisher bookingEventPublisher = org.mockito.Mockito.mock(BookingEventPublisher.class);
    private final BookingService bookingService = new BookingService(bookingRepository, bookingEventPublisher);

    @Test
    void createBookingPersistsPendingBookingAndPublishesEvent() {
        CreateBookingRequest request = new CreateBookingRequest(
                "user-001",
                "movie-001",
                "showtime-001",
                "Demo Movie",
                2,
                BigDecimal.valueOf(200000));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId("booking-001");
            return booking;
        });

        Booking booking = bookingService.createBooking(request);

        assertThat(booking.getId()).isEqualTo("booking-001");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getMovieTitle()).isEqualTo("Demo Movie");
        verify(bookingEventPublisher).publishBookingCreated(booking);
    }

    @Test
    void confirmBookingUpdatesStatus() {
        Booking booking = new Booking(
                "user-001",
                "movie-001",
                "showtime-001",
                "Demo Movie",
                1,
                BigDecimal.valueOf(100000));
        booking.setId("booking-001");
        when(bookingRepository.findById("booking-001")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updatedBooking = bookingService.confirmBooking("booking-001");

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(bookingRepository).save(booking);
    }
}
