package service;

import dao.TrainerDao;
import dao.TrainerDaoSql;
import dao.TrainerReviewDao;
import dao.TrainerReviewDaoSql;
import model.TrainerReview;
import model.TrainerReviewView;

import java.util.List;

public class TrainerReviewService {

    private final TrainerReviewDao reviewDao;
    private final TrainerDao trainerDao;

    public TrainerReviewService() {
        this.reviewDao = new TrainerReviewDaoSql();
        this.trainerDao = new TrainerDaoSql();
    }

    public List<TrainerReviewView> getReviews(int trainerId) {
        if (trainerId <= 0) {
            return List.of();
        }
        return reviewDao.getReviewsForTrainer(trainerId);
    }

    public String createReview(TrainerReview review, int playerId) {
        if (playerId <= 0) {
            return "Authentication required.";
        }

        if (review.getTrainerId() <= 0) {
            return "trainerId is required.";
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            return "Rating must be between 1 and 5.";
        }

        if (review.getComment() != null && review.getComment().length() > 2000) {
            return "Review comment is too long (max 2000 characters).";
        }

        if (reviewDao.trainerHasReviewFromPlayer(review.getTrainerId(), playerId)) {
            return "You have already reviewed this coach.";
        }

        // prevent reviewing yourself (coach reviewing own profile)
        var trainer = trainerDao.getTrainerById(review.getTrainerId());
        if (trainer != null && trainer.getUserId() == playerId) {
            return "You cannot review your own coach profile.";
        }

        review.setPlayerId(playerId);

        return reviewDao.createReview(review) > 0 ? null : "Review could not be saved.";
    }
}

