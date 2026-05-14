package com.kttkpm.bookingservice.service;

import com.kttkpm.bookingservice.domain.Booking;
import com.kttkpm.bookingservice.domain.BookingStatus;
import com.kttkpm.bookingservice.dto.CreateBookingRequest;
import com.kttkpm.bookingservice.repository.BookingRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventPublisher bookingEventPublisher;

    public BookingService(BookingRepository bookingRepository, BookingEventPublisher bookingEventPublisher) {
        this.bookingRepository = bookingRepository;
        this.bookingEventPublisher = bookingEventPublisher;
    }

    public Booking createBooking(CreateBookingRequest request) {
        Booking booking = new Booking(
                request.userId(),
                request.movieId(),
                request.showTimeId(),
                request.movieTitle(),
                request.seats(),
                request.totalPrice());
        Booking savedBooking = bookingRepository.save(booking);
        bookingEventPublisher.publishBookingCreated(savedBooking);
        return savedBooking;
    }

    public Booking getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    public List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public Booking confirmBooking(String bookingId) {
        return updateStatus(bookingId, BookingStatus.CONFIRMED);
    }

    public Booking failBooking(String bookingId) {
        return updateStatus(bookingId, BookingStatus.FAILED);
    }

    private Booking updateStatus(String bookingId, BookingStatus status) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == status) {
            return booking;
        }
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}
