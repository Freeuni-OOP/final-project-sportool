package service;

import dao.TrainerBookingDao;
import dao.TrainerBookingDaoSql;
import dao.TrainerDao;
import dao.TrainerDaoSql;
import dao.TrainerVenueDao;
import dao.TrainerVenueDaoSql;
import model.TrainerBooking;
import model.TrainerBookingView;
import model.TrainerVenue;

import java.time.LocalDate;
import java.util.List;

public class TrainerBookingService {

    private final TrainerBookingDao bookingDao;
    private final TrainerVenueDao venueDao;
    private final TrainerDao trainerDao;

    public TrainerBookingService() {
        this.bookingDao = new TrainerBookingDaoSql();
        this.venueDao = new TrainerVenueDaoSql();
        this.trainerDao = new TrainerDaoSql();
    }

    public TrainerBookingService(TrainerBookingDao bookingDao, TrainerVenueDao venueDao, TrainerDao trainerDao) {
        this.bookingDao = bookingDao;
        this.venueDao = venueDao;
        this.trainerDao = trainerDao;
    }

    public String createBookingRequest(TrainerBooking booking, int playerId) {
        if (playerId <= 0) {
            return "Authentication required.";
        }

        if (booking.getTrainerVenueId() <= 0) {
            return "A training venue must be selected.";
        }

        if (booking.getRequestedDate() == null) {
            return "Requested date is required.";
        }

        if (!booking.getRequestedDate().isAfter(LocalDate.now())) {
            return "Requested date must be in the future.";
        }

        TrainerVenue venue = venueDao.getVenueById(booking.getTrainerVenueId());
        if (venue == null) {
            return "Selected venue was not found.";
        }

        booking.setTrainerId(venue.getTrainerId());
        booking.setPlayerId(playerId);
        booking.setStatus("PENDING");

        return bookingDao.createBooking(booking) > 0
                ? null
                : "Booking request could not be created.";
    }

    public String updateBookingStatus(int bookingId, int userId, String status) {
        var trainer = trainerDao.getTrainerByUserId(userId);
        if (trainer == null) {
            return "Only coaches can manage booking requests.";
        }

        if (!"CONFIRMED".equals(status) && !"DECLINED".equals(status)) {
            return "Status must be CONFIRMED or DECLINED.";
        }

        return bookingDao.updateStatus(bookingId, trainer.getId(), status)
                ? null
                : "Booking request could not be updated.";
    }

    public List<TrainerBookingView> getPlayerBookings(int playerId) {
        if (playerId <= 0) {
            return List.of();
        }
        return bookingDao.getBookingsByPlayerId(playerId);
    }

    public List<TrainerBookingView> getTrainerBookings(int userId) {
        var trainer = trainerDao.getTrainerByUserId(userId);
        if (trainer == null) {
            return List.of();
        }
        return bookingDao.getBookingsByTrainerId(trainer.getId());
    }
}
