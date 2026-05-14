package com.kttkpm.bookingservice.repository;

import com.kttkpm.bookingservice.domain.Booking;
import com.kttkpm.bookingservice.domain.BookingStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);
}
