package dao;

import model.Trainer;
import java.util.List;

public interface TrainerDao {
    boolean addTrainer(Trainer trainer);
    boolean updateTrainer(Trainer trainer);
    boolean updateRating(int trainerId, double newRating, int newReviewCount);
    List<Trainer> getAllTrainers();
    Trainer getTrainerById(int id);
    Trainer getTrainerByUserId(int userId);
}
