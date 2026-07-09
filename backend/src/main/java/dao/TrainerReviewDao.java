package dao;

import model.TrainerReview;
import model.TrainerReviewView;

import java.util.List;

public interface TrainerReviewDao {
    int createReview(TrainerReview review);
    List<TrainerReviewView> getReviewsForTrainer(int trainerId);
    boolean trainerHasReviewFromPlayer(int trainerId, int playerId);
}

