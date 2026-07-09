package dao;

import model.TrainerVenue;

import java.util.List;

public interface TrainerVenueDao {
    int createVenue(TrainerVenue venue);
    boolean updateVenue(TrainerVenue venue);
    boolean deleteVenue(int venueId, int trainerId);
    TrainerVenue getVenueById(int venueId);
    List<TrainerVenue> getVenuesByTrainerId(int trainerId);
    boolean venueBelongsToTrainer(int venueId, int trainerId);
}
