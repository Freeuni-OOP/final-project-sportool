package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {
    @Test
    public void testFullConstructorAndGetters() {

        LocalDateTime start = LocalDateTime.of(2026, 6, 20, 18, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 20, 20, 0);

        Booking booking = new Booking(101, 5, 12, start, end, 90.0, "CONFIRMED");

        assertEquals(101, booking.getId());
        assertEquals(5, booking.getUserId());
        assertEquals(12, booking.getCourtId());
        assertEquals(start, booking.getStartTime());
        assertEquals(end, booking.getEndTime());
        assertEquals(90.0, booking.getTotalPrice());
        assertEquals("CONFIRMED", booking.getStatus());
    }

    @Test
    public void testEmptyConstructorAndSetters() {

        Booking booking = new Booking();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        booking.setId(202);
        booking.setUserId(8);
        booking.setCourtId(15);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setTotalPrice(120.50);
        booking.setStatus("PENDING");


        assertEquals(202, booking.getId());
        assertEquals(8, booking.getUserId());
        assertEquals(15, booking.getCourtId());
        assertEquals(start, booking.getStartTime());
        assertEquals(end, booking.getEndTime());
        assertEquals(120.50, booking.getTotalPrice());
        assertEquals("PENDING", booking.getStatus());
    }
}
