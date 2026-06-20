package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerTest {
    @Test
    public void testFullConstructorAndGetters() {

        Trainer trainer = new Trainer(1, 10, "nika", "dodashvili", "555555555", "MMA", 80.0, 5, 15);


        assertEquals(1, trainer.getId());
        assertEquals(10, trainer.getUserId());
        assertEquals("nika", trainer.getFirstName());
        assertEquals("dodashvili", trainer.getLastName());
        assertEquals("555555555", trainer.getPhone());
        assertEquals("MMA", trainer.getSportType());
        assertEquals(80.0, trainer.getPricePerSession());
        assertEquals(5, trainer.getRating());
        assertEquals(15, trainer.getReviewCount());
    }

    @Test
    public void testEmptyConstructorAndSetters() {

        Trainer trainer = new Trainer();

        trainer.setId(2);
        trainer.setUserId(20);
        trainer.setFirstName("Lasha");
        trainer.setLastName("Talakhadze");
        trainer.setPhone("555443322");
        trainer.setSportType("WEIGHTLIFTING");
        trainer.setPricePerSession(100.0);
        trainer.setRating(5.0);
        trainer.setReviewCount(42);


        assertEquals(2, trainer.getId());
        assertEquals(20, trainer.getUserId());
        assertEquals("Lasha", trainer.getFirstName());
        assertEquals("Talakhadze", trainer.getLastName());
        assertEquals("555443322", trainer.getPhone());
        assertEquals("WEIGHTLIFTING", trainer.getSportType());
        assertEquals(100.0, trainer.getPricePerSession());
        assertEquals(5.0, trainer.getRating());
        assertEquals(42, trainer.getReviewCount());
    }
}
