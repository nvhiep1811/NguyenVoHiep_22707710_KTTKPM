package com.kttkpm.bookingservice.controller;

import com.kttkpm.bookingservice.domain.BookingStatus;
import com.kttkpm.bookingservice.dto.BookingResponse;
import com.kttkpm.bookingservice.dto.CreateBookingRequest;
import com.kttkpm.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return BookingResponse.from(bookingService.createBooking(request));
    }

    @GetMapping
    public List<BookingResponse> getAll() {
        return bookingService.getAllBookings().stream()
                .map(BookingResponse::from)
                .toList();
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@PathVariable String bookingId) {
        return BookingResponse.from(bookingService.getBooking(bookingId));
    }

    @GetMapping("/users/{userId}")
    public List<BookingResponse> getByUserId(@PathVariable String userId) {
        return bookingService.getBookingsByUser(userId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @GetMapping("/status/{status}")
    public List<BookingResponse> getByStatus(@PathVariable BookingStatus status) {
        return bookingService.getBookingsByStatus(status).stream()
                .map(BookingResponse::from)
                .toList();
    }
}
