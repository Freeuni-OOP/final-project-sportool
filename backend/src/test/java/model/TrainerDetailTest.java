package model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainerDetailTest {

    @Test
    public void testTrainerDetailSetters() {
        TrainerDetail detail = new TrainerDetail();
        List<TrainerVenue> venueList = new ArrayList<>();

        detail.setId(1);
        detail.setUserId(10);
        detail.setFirstName("Luka");
        detail.setLastName("K");
        detail.setPhone("555123456");
        detail.setSportType("Tennis");
        detail.setPricePerSession(45.5);
        detail.setRating(4.8);
        detail.setReviewCount(12);
        detail.setDescription("Experienced coach");
        detail.setVenues(venueList);

        assertEquals(1, detail.getId());
        assertEquals(10, detail.getUserId());
        assertEquals("Luka", detail.getFirstName());
        assertEquals("K", detail.getLastName());
        assertEquals("555123456", detail.getPhone());
        assertEquals("Tennis", detail.getSportType());
        assertEquals(45.5, detail.getPricePerSession());
        assertEquals(4.8, detail.getRating());
        assertEquals(12, detail.getReviewCount());
        assertEquals("Experienced coach", detail.getDescription());
        assertEquals(venueList, detail.getVenues());
    }

    @Test
    public void testTrainerDetailGettersAndNullVenues() {
        TrainerDetail detail = new TrainerDetail();
        detail.setId(5);
        detail.setVenues(null);

        int id = detail.getId();
        List<TrainerVenue> venues = detail.getVenues();

        assertEquals(5, id);
        assertNotNull(venues);
        assertEquals(0, venues.size());
    }
}