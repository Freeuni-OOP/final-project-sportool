package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserBookingViewTest {

    @Test
    public void testUserBookingViewSetters() {
        UserBookingView view = new UserBookingView();
        LocalDateTime start = LocalDateTime.of(2026, 7, 9, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 9, 12, 0);

        view.setId(1);
        view.setCourtId(10);
        view.setCourtName("Grand Court");
        view.setLocation("Vake");
        view.setCourtType("Clay");
        view.setStartTime(start);
        view.setEndTime(end);
        view.setTotalPrice(40.0);
        view.setStatus("CONFIRMED");
        view.setPaymentStatus("PAID");

        assertEquals(1, view.getId());
        assertEquals(10, view.getCourtId());
        assertEquals("Grand Court", view.getCourtName());
        assertEquals("Vake", view.getLocation());
        assertEquals("Clay", view.getCourtType());
        assertEquals(start, view.getStartTime());
        assertEquals(end, view.getEndTime());
        assertEquals(40.0, view.getTotalPrice());
        assertEquals("CONFIRMED", view.getStatus());
        assertEquals("PAID", view.getPaymentStatus());
    }

    @Test
    public void testUserBookingViewGetters() {
        UserBookingView view = new UserBookingView();
        view.setId(5);

        int id = view.getId();
        assertEquals(5, id);
    }
}