package service;

import dao.TrainerDao;
import dao.TrainerDaoSql;
import dao.TrainerVenueDao;
import dao.TrainerVenueDaoSql;
import model.AvailabilitySlot;
import model.TrainerVenue;

import java.util.List;
import java.util.Set;

public class TrainerVenueService {

    private static final Set<String> VALID_DAYS = Set.of(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    );

    private final TrainerVenueDao venueDao;
    private final TrainerDao trainerDao;

    public TrainerVenueService() {
        this.venueDao = new TrainerVenueDaoSql();
        this.trainerDao = new TrainerDaoSql();
    }

    public TrainerVenueService(TrainerVenueDao venueDao, TrainerDao trainerDao) {
        this.venueDao = venueDao;
        this.trainerDao = trainerDao;
    }

    public List<TrainerVenue> getVenuesForTrainer(int trainerId) {
        if (trainerId <= 0) {
            return List.of();
        }
        return venueDao.getVenuesByTrainerId(trainerId);
    }

    public String createVenue(TrainerVenue venue, int userId) {
        Integer trainerId = resolveTrainerId(venue.getTrainerId(), userId);
        if (trainerId == null) {
            return "Only coaches can manage training venues.";
        }

        String validationError = validateVenue(venue);
        if (validationError != null) {
            return validationError;
        }

        venue.setTrainerId(trainerId);
        return venueDao.createVenue(venue) > 0 ? null : "Venue could not be created.";
    }

    public String updateVenue(TrainerVenue venue, int userId) {
        Integer trainerId = resolveTrainerId(venue.getTrainerId(), userId);
        if (trainerId == null) {
            return "Only coaches can manage training venues.";
        }

        if (!venueDao.venueBelongsToTrainer(venue.getId(), trainerId)) {
            return "Venue not found or access denied.";
        }

        String validationError = validateVenue(venue);
        if (validationError != null) {
            return validationError;
        }

        venue.setTrainerId(trainerId);
        return venueDao.updateVenue(venue) ? null : "Venue could not be updated.";
    }

    public String deleteVenue(int venueId, int userId) {
        var trainer = trainerDao.getTrainerByUserId(userId);
        if (trainer == null) {
            return "Only coaches can manage training venues.";
        }

        return venueDao.deleteVenue(venueId, trainer.getId())
                ? null
                : "Venue could not be deleted.";
    }

    private Integer resolveTrainerId(int requestedTrainerId, int userId) {
        var trainer = trainerDao.getTrainerByUserId(userId);
        if (trainer == null) {
            return null;
        }
        if (requestedTrainerId > 0 && requestedTrainerId != trainer.getId()) {
            return null;
        }
        return trainer.getId();
    }

    private String validateVenue(TrainerVenue venue) {
        if (venue.getVenueName() == null || venue.getVenueName().isBlank()) {
            return "Venue name is required.";
        }

        if (venue.getPriceOverride() != null && venue.getPriceOverride() <= 0) {
            return "Price override must be positive when provided.";
        }

        if (venue.getAvailability() == null || venue.getAvailability().isEmpty()) {
            return "At least one availability slot is required.";
        }

        for (AvailabilitySlot slot : venue.getAvailability()) {
            if (slot.getDayOfWeek() == null || !VALID_DAYS.contains(slot.getDayOfWeek().toUpperCase())) {
                return "Each availability slot must include a valid day of week.";
            }
            slot.setDayOfWeek(slot.getDayOfWeek().toUpperCase());

            if (slot.getStartTime() == null || slot.getStartTime().isBlank()
                    || slot.getEndTime() == null || slot.getEndTime().isBlank()) {
                return "Each availability slot must include start and end times.";
            }
        }

        return null;
    }
}
