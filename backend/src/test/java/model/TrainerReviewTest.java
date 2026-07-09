package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerReviewTest {

    @Test
    public void testTrainerReviewSetters() {
        TrainerReview review = new TrainerReview();
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 20, 50);

        review.setId(1);
        review.setTrainerId(10);
        review.setPlayerId(20);
        review.setRating(5);
        review.setComment("Great coach!");
        review.setCreatedAt(now);

        assertEquals(1, review.getId());
        assertEquals(10, review.getTrainerId());
        assertEquals(20, review.getPlayerId());
        assertEquals(5, review.getRating());
        assertEquals("Great coach!", review.getComment());
        assertEquals(now, review.getCreatedAt());
    }

    @Test
    public void testTrainerReviewGetters() {
        TrainerReview review = new TrainerReview();
        review.setId(99);

        int id = review.getId();
        assertEquals(99, id);
    }
}