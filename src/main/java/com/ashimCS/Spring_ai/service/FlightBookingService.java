package com.ashimCS.Spring_ai.service;

import com.ashimCS.Spring_ai.entity.BookingStatus;
import com.ashimCS.Spring_ai.entity.FlightBooking;
import com.ashimCS.Spring_ai.repository.FlightBookingRepository;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


// FlightBookingService.java
@Service
@RequiredArgsConstructor
@Transactional
public class FlightBookingService {

    private final FlightBookingRepository repository;

    //Checks whether the user already has the same booking. If no → creates a new FlightBooking. Saves it to the database.
    public FlightBooking createBooking(String userId, String destination, Instant departureTime) {
        boolean exists = repository.existsByUserIdAndDestinationAndDepartureTime(
                userId, destination, departureTime);

        if (exists) {
            throw new IllegalArgumentException(
                    "You already have a booking to " + destination + " on that date.");
        }

        FlightBooking booking = FlightBooking.builder()
                .userId(userId)
                .destination(destination)
                .departureTime(departureTime)
                .bookingStatus(BookingStatus.CONFIRMED)
                .build();

        return repository.save(booking);
    }

    // Simply fetches all bookings of that user, sorted by departure time.
    public List<FlightBooking> getUserBookings(String userId) {
        return repository.findByUserIdOrderByDepartureTimeDesc(userId);
    }

    //Finds the booking., Checks that the booking belongs to the user., Changes its status. Saves the updated booking.
    public FlightBooking updateBookingStatus(Long bookingId, String userId, BookingStatus newStatus) {
        FlightBooking booking = repository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only modify your own bookings");
        }

        booking.setBookingStatus(newStatus);
        return repository.save(booking);
    }
}
