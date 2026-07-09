package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerBookingTest {

    @Test
    public void testTrainerBookingSetters() {
        TrainerBooking booking = new TrainerBooking();

        int id = 101;
        int trainerId = 12;
        int trainerVenueId = 4;
        int playerId = 55;
        LocalDate requestedDate = LocalDate.of(2026, 7, 15);
        String requestedTimeSlot = "18:00 - 19:00";
        String status = "CONFIRMED";
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 14, 30, 0);

        booking.setId(id);
        booking.setTrainerId(trainerId);
        booking.setTrainerVenueId(trainerVenueId);
        booking.setPlayerId(playerId);
        booking.setRequestedDate(requestedDate);
        booking.setRequestedTimeSlot(requestedTimeSlot);
        booking.setStatus(status);
        booking.setCreatedAt(createdAt);

        assertEquals(id, booking.getId());
        assertEquals(trainerId, booking.getTrainerId());
        assertEquals(trainerVenueId, booking.getTrainerVenueId());
        assertEquals(playerId, booking.getPlayerId());
        assertEquals(requestedDate, booking.getRequestedDate());
        assertEquals(requestedTimeSlot, booking.getRequestedTimeSlot());
        assertEquals(status, booking.getStatus());
        assertEquals(createdAt, booking.getCreatedAt());
    }

    @Test
    public void testTrainerBookingGetters() {
        TrainerBooking booking = new TrainerBooking();

        booking.setId(202);
        booking.setTrainerId(15);
        booking.setTrainerVenueId(8);
        booking.setPlayerId(99);
        booking.setRequestedDate(LocalDate.of(2026, 8, 20));
        booking.setRequestedTimeSlot("12:00 - 13:00");
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.of(2026, 7, 9, 16, 0, 0));

        int currentId = booking.getId();
        int currentTrainerId = booking.getTrainerId();
        int currentVenueId = booking.getTrainerVenueId();
        int currentPlayerId = booking.getPlayerId();
        LocalDate currentDate = booking.getRequestedDate();
        String currentTimeSlot = booking.getRequestedTimeSlot();
        String currentStatus = booking.getStatus();
        LocalDateTime currentCreatedAt = booking.getCreatedAt();

        assertEquals(202, currentId);
        assertEquals(15, currentTrainerId);
        assertEquals(8, currentVenueId);
        assertEquals(99, currentPlayerId);
        assertEquals(LocalDate.of(2026, 8, 20), currentDate);
        assertEquals("12:00 - 13:00", currentTimeSlot);
        assertEquals("PENDING", currentStatus);
        assertEquals(LocalDateTime.of(2026, 7, 9, 16, 0, 0), currentCreatedAt);
    }
}