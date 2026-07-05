package service;

import dao.BookingDaoSql;
import model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    private BookingService bookingService;
    private FakeBookingDao fakeBookingDao;

    @BeforeEach
    public void setUp() {
        fakeBookingDao = new FakeBookingDao();
        bookingService = new BookingService(fakeBookingDao);
    }

    @Test
    public void testMakeBookingSuccess() {
        Booking booking = new Booking();
        booking.setCourtId(1);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        booking.setPaymentReference("PAY-TEST123");

        fakeBookingDao.setAvailable(true);
        fakeBookingDao.setCreateSuccess(true);

        String result = bookingService.makeBooking(booking);

        assertNull(result);
    }

    @Test
    public void testMakeBookingRequiresPayment() {
        Booking booking = new Booking();
        booking.setCourtId(1);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        String result = bookingService.makeBooking(booking);

        assertNotNull(result);
        assertEquals("Payment is required before confirming a booking.", result);
    }

    @Test
    public void testMakeBookingCourtAlreadyBooked() {
        Booking booking = new Booking();
        booking.setCourtId(1);
        booking.setStartTime(LocalDateTime.now().plusDays(1));
        booking.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        booking.setPaymentReference("PAY-TEST123");

        fakeBookingDao.setAvailable(false);

        String result = bookingService.makeBooking(booking);

        assertNotNull(result);
        assertEquals("The selected court is already booked for this time slot.", result);
    }

    private static class FakeBookingDao extends BookingDaoSql {
        private boolean isAvailable;
        private boolean isCreateSuccess;

        public void setAvailable(boolean available) {
            this.isAvailable = available;
        }

        public void setCreateSuccess(boolean success) {
            this.isCreateSuccess = success;
        }

        @Override
        public boolean isCourtAvailable(int courtId, LocalDateTime start, LocalDateTime end) {
            return isAvailable;
        }

        @Override
        public boolean createBooking(Booking booking) {
            return isCreateSuccess;
        }
    }
}
