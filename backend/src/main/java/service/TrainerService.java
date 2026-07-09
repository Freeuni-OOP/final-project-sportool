package service;

import dao.TrainerDao;
import dao.TrainerDaoSql;
import dao.TrainerProfileDao;
import dao.TrainerProfileDaoSql;
import dao.TrainerVenueDao;
import dao.TrainerVenueDaoSql;
import model.Trainer;
import model.TrainerDetail;
import model.TrainerVenue;

import java.util.List;
import java.util.stream.Collectors;

public class TrainerService {

    private final TrainerDao trainerDao;
    private final TrainerVenueDao venueDao;
    private final TrainerProfileDao profileDao;

    public TrainerService() {
        this.trainerDao = new TrainerDaoSql();
        this.venueDao = new TrainerVenueDaoSql();
        this.profileDao = new TrainerProfileDaoSql();
    }

    public TrainerService(TrainerDao trainerDao) {
        this(trainerDao, new TrainerVenueDaoSql(), new TrainerProfileDaoSql());
    }

    public TrainerService(TrainerDao trainerDao, TrainerVenueDao venueDao, TrainerProfileDao profileDao) {
        this.trainerDao = trainerDao;
        this.venueDao = venueDao;
        this.profileDao = profileDao;
    }

    public List<Trainer> getAllTrainers() {
        return trainerDao.getAllTrainers();
    }

    public Trainer getTrainerById(int id) {
        if (id <= 0) return null;
        return trainerDao.getTrainerById(id);
    }

    public Trainer getTrainerByUserId(int userId) {
        if (userId <= 0) {
            return null;
        }
        return trainerDao.getTrainerByUserId(userId);
    }

    public List<TrainerDetail> getAllTrainerDetails() {
        return trainerDao.getAllTrainers().stream()
                .map(this::toDetail)
                .filter(this::isPublishableTrainer)
                .collect(Collectors.toList());
    }

    public TrainerDetail getTrainerDetailById(int id) {
        Trainer trainer = getTrainerById(id);
        return trainer == null ? null : toDetail(trainer);
    }

    public Trainer ensureTrainerProfile(int userId, String fullName) {
        Trainer existing = trainerDao.getTrainerByUserId(userId);
        if (existing != null) {
            return existing;
        }

        Trainer trainer = new Trainer();
        trainer.setUserId(userId);
        String[] nameParts = splitName(fullName);
        trainer.setFirstName(nameParts[0]);
        trainer.setLastName(nameParts[1]);
        trainer.setPhone("Not set");
        trainer.setSportType("Tennis");
        trainer.setPricePerSession(50.0);

        if (trainerDao.addTrainer(trainer)) {
            return trainerDao.getTrainerByUserId(userId);
        }
        return null;
    }

    public String updateTrainerProfile(Trainer trainer, int userId) {
        Trainer existing = trainerDao.getTrainerByUserId(userId);
        if (existing == null) {
            return "Trainer profile not found.";
        }

        if (trainer.getPhone() == null || trainer.getPhone().isBlank()) {
            return "Phone number is required.";
        }
        if (trainer.getSportType() == null || trainer.getSportType().isBlank()) {
            return "Sport type is required.";
        }
        if (trainer.getPricePerSession() <= 0) {
            return "Price per session must be positive.";
        }

        existing.setPhone(trainer.getPhone());
        existing.setSportType(trainer.getSportType());
        existing.setPricePerSession(trainer.getPricePerSession());
        existing.setFirstName(trainer.getFirstName() != null ? trainer.getFirstName() : existing.getFirstName());
        existing.setLastName(trainer.getLastName() != null ? trainer.getLastName() : existing.getLastName());

        return trainerDao.updateTrainer(existing) ? null : "Trainer profile could not be updated.";
    }

    public boolean addTrainer(Trainer trainer) {
        if (trainer.getFirstName() == null || trainer.getFirstName().isEmpty()) return false;
        if (trainer.getLastName() == null || trainer.getLastName().isEmpty()) return false;
        if (trainer.getPhone() == null || trainer.getPhone().isEmpty()) return false;
        if (trainer.getSportType() == null || trainer.getSportType().isEmpty()) return false;
        if (trainer.getPricePerSession() <= 0) return false;
        return trainerDao.addTrainer(trainer);
    }

    private TrainerDetail toDetail(Trainer trainer) {
        TrainerDetail detail = new TrainerDetail(trainer);
        List<TrainerVenue> venues = venueDao.getVenuesByTrainerId(trainer.getId());
        List<TrainerVenue> publishableVenues = venues.stream()
                .filter(venue -> venue.getAvailability() != null && !venue.getAvailability().isEmpty())
                .collect(Collectors.toList());

        detail.setVenues(publishableVenues);
        detail.setDescription(profileDao.getDescription(trainer.getId()));
        return detail;
    }

    private boolean isPublishableTrainer(TrainerDetail trainer) {
        return trainer != null
                && trainer.getVenues() != null
                && !trainer.getVenues().isEmpty();
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Coach", "User"};
        }

        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex < 0) {
            return new String[]{trimmed, "Coach"};
        }

        return new String[]{
                trimmed.substring(0, spaceIndex),
                trimmed.substring(spaceIndex + 1).trim()
        };
    }
}