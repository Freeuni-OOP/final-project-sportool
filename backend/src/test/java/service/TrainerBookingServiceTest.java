package service;

import dao.TrainerBookingDao;
import dao.TrainerDao;
import dao.TrainerVenueDao;
import model.TrainerBooking;
import model.TrainerBookingView;
import model.TrainerVenue;
import model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainerBookingServiceTest {

    @Mock private TrainerBookingDao bookingDao;
    @Mock private TrainerVenueDao venueDao;
    @Mock private TrainerDao trainerDao;

    @InjectMocks
    private TrainerBookingService service;

    @BeforeEach
    void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructorsAndPlayerBookings() {
        assertNotNull(new TrainerBookingService());
        assertEquals(List.of(), service.getPlayerBookings(0));

        List<TrainerBookingView> mockList = List.of(mock(TrainerBookingView.class));
        when(bookingDao.getBookingsByPlayerId(1)).thenReturn(mockList);
        assertEquals(mockList, service.getPlayerBookings(1));
    }

    @Test
    void testCreateBookingFailures() {
        TrainerBooking b = mock(TrainerBooking.class);

        assertEquals("Authentication required.", service.createBookingRequest(b, 0));

        when(b.getTrainerVenueId()).thenReturn(0);
        assertEquals("A training venue must be selected.", service.createBookingRequest(b, 1));

        when(b.getTrainerVenueId()).thenReturn(5);
        when(b.getRequestedDate()).thenReturn(null);
        assertEquals("Requested date is required.", service.createBookingRequest(b, 1));

        when(b.getRequestedDate()).thenReturn(LocalDate.now().minusDays(1));
        assertEquals("Requested date must be in the future.", service.createBookingRequest(b, 1));

        when(b.getRequestedDate()).thenReturn(LocalDate.now().plusDays(1));
        when(venueDao.getVenueById(5)).thenReturn(null);
        assertEquals("Selected venue was not found.", service.createBookingRequest(b, 1));

        TrainerVenue venue = mock(TrainerVenue.class);
        when(venueDao.getVenueById(5)).thenReturn(venue);
        when(bookingDao.createBooking(b)).thenReturn(0);
        assertEquals("Booking request could not be created.", service.createBookingRequest(b, 1));
    }

    @Test
    void testCreateBookingSuccess() {
        TrainerBooking b = mock(TrainerBooking.class);
        when(b.getTrainerVenueId()).thenReturn(5);
        when(b.getRequestedDate()).thenReturn(LocalDate.now().plusDays(1));

        TrainerVenue venue = mock(TrainerVenue.class);
        when(venueDao.getVenueById(5)).thenReturn(venue);
        when(bookingDao.createBooking(b)).thenReturn(1);

        assertNull(service.createBookingRequest(b, 1));
    }

    @Test
    void testUpdateBookingStatus() {
        assertEquals("Only coaches can manage booking requests.", service.updateBookingStatus(1, 1, "CONFIRMED"));

        var trainer = mock(Trainer.class);
        when(trainer.getId()).thenReturn(10);
        when(trainerDao.getTrainerByUserId(1)).thenReturn(trainer);

        assertEquals("Status must be CONFIRMED or DECLINED.", service.updateBookingStatus(1, 1, "INVALID"));

        when(bookingDao.updateStatus(1, 10, "CONFIRMED")).thenReturn(false);
        assertEquals("Booking request could not be updated.", service.updateBookingStatus(1, 1, "CONFIRMED"));

        when(bookingDao.updateStatus(1, 10, "CONFIRMED")).thenReturn(true);
        assertNull(service.updateBookingStatus(1, 1, "CONFIRMED"));
    }

    @Test
    void testGetTrainerBookings() {
        when(trainerDao.getTrainerByUserId(1)).thenReturn(null);
        assertEquals(List.of(), service.getTrainerBookings(1));

        var trainer = mock(Trainer.class);
        when(trainer.getId()).thenReturn(10);
        when(trainerDao.getTrainerByUserId(1)).thenReturn(trainer);

        List<TrainerBookingView> mockList = List.of(mock(TrainerBookingView.class));
        when(bookingDao.getBookingsByTrainerId(10)).thenReturn(mockList);
        assertEquals(mockList, service.getTrainerBookings(1));
    }
}