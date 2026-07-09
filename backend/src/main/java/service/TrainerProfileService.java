package service;

import dao.TrainerDao;
import dao.TrainerDaoSql;
import dao.TrainerProfileDao;
import dao.TrainerProfileDaoSql;

public class TrainerProfileService {

    private final TrainerDao trainerDao;
    private final TrainerProfileDao profileDao;

    public TrainerProfileService() {
        this.trainerDao = new TrainerDaoSql();
        this.profileDao = new TrainerProfileDaoSql();
    }

    public String getDescriptionForTrainer(int trainerId) {
        if (trainerId <= 0) {
            return null;
        }
        return profileDao.getDescription(trainerId);
    }

    public String upsertMyDescription(int userId, String description) {
        var trainer = trainerDao.getTrainerByUserId(userId);
        if (trainer == null) {
            return "Only coaches can edit their description.";
        }

        if (description != null && description.length() > 1500) {
            return "Description is too long (max 1500 characters).";
        }

        return profileDao.upsertDescription(trainer.getId(), description) ? null : "Description could not be saved.";
    }
}

