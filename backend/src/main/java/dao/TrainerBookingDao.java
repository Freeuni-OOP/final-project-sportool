package dao;

import model.TrainerBooking;
import model.TrainerBookingView;

import java.util.List;

public interface TrainerBookingDao {
    int createBooking(TrainerBooking booking);
    boolean updateStatus(int bookingId, int trainerId, String status);
    List<TrainerBookingView> getBookingsByPlayerId(int playerId);
    List<TrainerBookingView> getBookingsByTrainerId(int trainerId);
    TrainerBookingView getBookingById(int bookingId);
}
