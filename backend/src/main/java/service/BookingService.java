package service;

import dao.BookingDaoSql;
import model.Booking;
import java.time.LocalDateTime;

public class BookingService {

    private final BookingDaoSql bookingDao;

    public BookingService() {
        this.bookingDao = new BookingDaoSql();
    }

    public BookingService(BookingDaoSql bookingDao) {
        this.bookingDao = bookingDao;
    }

    public boolean makeBooking(Booking booking) {
        boolean isAvailable = bookingDao.isCourtAvailable(
                booking.getCourtId(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (!isAvailable) {
            return false;
        }

        return bookingDao.createBooking(booking);
    }

}