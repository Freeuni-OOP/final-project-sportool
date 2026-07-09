package model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainerVenueTest {

    @Test
    public void testTrainerVenueSetters() {
        TrainerVenue venue = new TrainerVenue();
        List<AvailabilitySlot> slots = new ArrayList<>();

        venue.setId(1);
        venue.setTrainerId(10);
        venue.setVenueName("Sport Complex");
        venue.setAddress("Chavchavadze Ave");
        venue.setPriceOverride(60.0);
        venue.setAvailability(slots);

        assertEquals(1, venue.getId());
        assertEquals(10, venue.getTrainerId());
        assertEquals("Sport Complex", venue.getVenueName());
        assertEquals("Chavchavadze Ave", venue.getAddress());
        assertEquals(60.0, venue.getPriceOverride());
        assertEquals(slots, venue.getAvailability());
    }

    @Test
    public void testTrainerVenueGettersAndNullAvailability() {
        TrainerVenue venue = new TrainerVenue();
        venue.setId(7);
        venue.setAvailability(null);

        int id = venue.getId();
        List<AvailabilitySlot> list = venue.getAvailability();

        assertEquals(7, id);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
}