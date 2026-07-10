package service;

import dao.TrainerDao;
import dao.TrainerVenueDao;
import model.AvailabilitySlot;
import model.Trainer;
import model.TrainerVenue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainerVenueServiceTest {

    @Mock private TrainerVenueDao venueDao;
    @Mock private TrainerDao trainerDao;
    @InjectMocks private TrainerVenueService service;

    private Trainer trainer;
    private TrainerVenue venue;
    private AvailabilitySlot slot;

    @BeforeEach
    void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);
        trainer = mock(Trainer.class);
        venue = mock(TrainerVenue.class);
        slot = mock(AvailabilitySlot.class);

        when(trainer.getId()).thenReturn(10);
        when(trainerDao.getTrainerByUserId(1)).thenReturn(trainer);
        when(venue.getTrainerId()).thenReturn(10);
        when(venue.getId()).thenReturn(100);
        when(venueDao.venueBelongsToTrainer(100, 10)).thenReturn(true);
    }

    @Test
    void testBasicMethodsAndGet() {
        assertNotNull(new TrainerVenueService(venueDao, trainerDao));
        assertEquals(List.of(), service.getVenuesForTrainer(0));
        when(venueDao.getVenuesByTrainerId(1)).thenReturn(List.of(venue));
        assertEquals(1, service.getVenuesForTrainer(1).size());
    }

    @Test
    void testAuthFailures() {
        when(trainerDao.getTrainerByUserId(1)).thenReturn(null);
        assertEquals("Only coaches can manage training venues.", service.createVenue(venue, 1));
        assertEquals("Only coaches can manage training venues.", service.updateVenue(venue, 1));
        assertEquals("Only coaches can manage training venues.", service.deleteVenue(100, 1));

        when(trainerDao.getTrainerByUserId(1)).thenReturn(trainer);

        when(venue.getTrainerId()).thenReturn(5);
        assertEquals("Only coaches can manage training venues.", service.createVenue(venue, 1));

        when(venue.getTrainerId()).thenReturn(10);

        when(venueDao.venueBelongsToTrainer(100, 10)).thenReturn(false);
        assertEquals("Venue not found or access denied.", service.updateVenue(venue, 1));
    }

    @Test
    void testValidationFlow() {
        when(venue.getVenueName()).thenReturn("");
        assertEquals("Venue name is required.", service.createVenue(venue, 1));
        assertEquals("Venue name is required.", service.updateVenue(venue, 1));

        when(venue.getVenueName()).thenReturn("Gym");
        when(venue.getPriceOverride()).thenReturn(0.0);
        assertEquals("Price override must be positive when provided.", service.createVenue(venue, 1));
        assertEquals("Price override must be positive when provided.", service.updateVenue(venue, 1));

        when(venue.getPriceOverride()).thenReturn(10.0);
        when(venue.getAvailability()).thenReturn(null);
        assertEquals("At least one availability slot is required.", service.createVenue(venue, 1));
        assertEquals("At least one availability slot is required.", service.updateVenue(venue, 1));

        when(venue.getAvailability()).thenReturn(List.of(slot));
        when(slot.getDayOfWeek()).thenReturn("INVALID");
        assertEquals("Each availability slot must include a valid day of week.", service.createVenue(venue, 1));
        assertEquals("Each availability slot must include a valid day of week.", service.updateVenue(venue, 1));

        when(slot.getDayOfWeek()).thenReturn("MONDAY");
        when(slot.getStartTime()).thenReturn("");
        assertEquals("Each availability slot must include start and end times.", service.createVenue(venue, 1));
        assertEquals("Each availability slot must include start and end times.", service.updateVenue(venue, 1));
    }

    @Test
    void testDBOperations() {
        mockValidVenue();

        when(venueDao.createVenue(venue)).thenReturn(0).thenReturn(1);
        assertEquals("Venue could not be created.", service.createVenue(venue, 1));
        assertNull(service.createVenue(venue, 1));

        when(venueDao.updateVenue(venue)).thenReturn(false).thenReturn(true);
        assertEquals("Venue could not be updated.", service.updateVenue(venue, 1));
        assertNull(service.updateVenue(venue, 1));

        when(venueDao.deleteVenue(100, 10)).thenReturn(false).thenReturn(true);
        assertEquals("Venue could not be deleted.", service.deleteVenue(100, 1));
        assertNull(service.deleteVenue(100, 1));
    }

    private void mockValidVenue() {
        when(venue.getVenueName()).thenReturn("Gym");
        when(venue.getPriceOverride()).thenReturn(10.0);
        when(venue.getAvailability()).thenReturn(List.of(slot));
        when(slot.getDayOfWeek()).thenReturn("MONDAY");
        when(slot.getStartTime()).thenReturn("10:00");
        when(slot.getEndTime()).thenReturn("11:00");
    }
}