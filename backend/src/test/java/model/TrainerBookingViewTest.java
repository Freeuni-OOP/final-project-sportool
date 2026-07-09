package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerBookingViewTest {

    @Test
    public void testTrainerBookingViewSetters() {
        TrainerBookingView view = new TrainerBookingView();
        view.setId(1);
        view.setTrainerId(10);
        view.setTrainerVenueId(5);
        view.setPlayerId(20);
        view.setPlayerName("Giorgi");
        view.setTrainerName("Luka");
        view.setVenueName("Arena 1");
        view.setVenueAddress("Tbilisi");
        view.setSportType("Basketball");
        view.setRequestedDate(LocalDate.of(2026, 7, 15));
        view.setRequestedTimeSlot("19:00");
        view.setStatus("PENDING");
        view.setCreatedAt(LocalDateTime.of(2026, 7, 9, 12, 0));
        view.setSessionPrice(50.0);

        assertEquals(1, view.getId());
        assertEquals(10, view.getTrainerId());
        assertEquals(5, view.getTrainerVenueId());
        assertEquals(20, view.getPlayerId());
        assertEquals("Giorgi", view.getPlayerName());
        assertEquals("Luka", view.getTrainerName());
        assertEquals("Arena 1", view.getVenueName());
        assertEquals("Tbilisi", view.getVenueAddress());
        assertEquals("Basketball", view.getSportType());
        assertEquals(LocalDate.of(2026, 7, 15), view.getRequestedDate());
        assertEquals("19:00", view.getRequestedTimeSlot());
        assertEquals("PENDING", view.getStatus());
        assertEquals(LocalDateTime.of(2026, 7, 9, 12, 0), view.getCreatedAt());
        assertEquals(50.0, view.getSessionPrice());
    }

    @Test
    public void testTrainerBookingViewGetters() {
        TrainerBookingView view = new TrainerBookingView();
        view.setId(2);
        int id = view.getId();
        assertEquals(2, id);
    }
}