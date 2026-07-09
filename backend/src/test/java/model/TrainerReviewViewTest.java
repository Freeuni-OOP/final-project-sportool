package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerReviewViewTest {

    @Test
    public void testTrainerReviewViewSetters() {
        TrainerReviewView view = new TrainerReviewView();
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 21, 0);

        view.setId(1);
        view.setTrainerId(10);
        view.setPlayerId(20);
        view.setPlayerName("Lasha");
        view.setRating(5);
        view.setComment("Excellent session");
        view.setCreatedAt(now);

        assertEquals(1, view.getId());
        assertEquals(10, view.getTrainerId());
        assertEquals(20, view.getPlayerId());
        assertEquals("Lasha", view.getPlayerName());
        assertEquals(5, view.getRating());
        assertEquals("Excellent session", view.getComment());
        assertEquals(now, view.getCreatedAt());
    }

    @Test
    public void testTrainerReviewViewGetters() {
        TrainerReviewView view = new TrainerReviewView();
        view.setId(55);

        int id = view.getId();
        assertEquals(55, id);
    }
}