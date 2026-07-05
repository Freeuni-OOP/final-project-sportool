package service;

import dao.BookingDaoSql;
import model.Booking;
import model.UserBookingView;

import java.time.LocalDate;
import java.util.List;

public class BookingService {

    private final BookingDaoSql bookingDao;

    public BookingService() {
        this.bookingDao = new BookingDaoSql();
    }

    public BookingService(BookingDaoSql bookingDao) {
        this.bookingDao = bookingDao;
    }

    public String makeBooking(Booking booking) {
        if (booking.getPaymentReference() == null || booking.getPaymentReference().isBlank()) {
            return "Payment is required before confirming a booking.";
        }

        boolean isAvailable = bookingDao.isCourtAvailable(
                booking.getCourtId(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (!isAvailable) {
            return "The selected court is already booked for this time slot.";
        }

        boolean isSaved = bookingDao.createBooking(booking);
        if (!isSaved) {
            return "Booking could not be saved. Please verify your account and court selection.";
        }

        return null;
    }

    public List<Booking> getCourtBookingsForDate(int courtId, LocalDate date) {
        return bookingDao.getBookingsForCourtOnDate(courtId, date);
    }

    public List<UserBookingView> getUserBookings(int userId) {
        return bookingDao.getBookingsByUserId(userId);
    }
}
