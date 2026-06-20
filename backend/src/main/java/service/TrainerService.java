package service;

import dao.TrainerDao;
import dao.TrainerDaoSql;
import model.Trainer;
import java.util.List;

public class TrainerService {

    private final TrainerDao trainerDao;

    public TrainerService() {
        this.trainerDao = new TrainerDaoSql();
    }

    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public List<Trainer> getAllTrainers() {
        return trainerDao.getAllTrainers();
    }

    public Trainer getTrainerById(int id) {
        if (id <= 0) return null;
        return trainerDao.getTrainerById(id);
    }

    public boolean addTrainer(Trainer trainer) {
        if (trainer.getFirstName() == null || trainer.getFirstName().isEmpty()) return false;
        if (trainer.getLastName() == null || trainer.getLastName().isEmpty()) return false;
        if (trainer.getPhone() == null || trainer.getPhone().isEmpty()) return false;
        if (trainer.getSportType() == null || trainer.getSportType().isEmpty()) return false;
        if (trainer.getPricePerSession() <= 0) return false;
        return trainerDao.addTrainer(trainer);
    }
}