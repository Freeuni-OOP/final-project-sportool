package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class courtTest {
    @Test
    void testCourtConstructorAndGetters() {

        Court court = new Court(1, "Dinamo", "FOOTBALL", "Tbilisi", 50.0);

        assertEquals(1, court.getId());
        assertEquals("Dinamo", court.getName());
        assertEquals("FOOTBALL", court.getType());
        assertEquals("Tbilisi", court.getLocation());
        assertEquals(50.0, court.getPricePerHour());
    }

    @Test
    void testCourtSetters() {

        Court court = new Court();

        court.setId(2);
        court.setName("Mziuri Tennis courts");
        court.setType("TENNIS");
        court.setLocation("Tbilisi");
        court.setPricePerHour(35);

        assertEquals(2, court.getId());
        assertEquals("Mziuri Tennis courts", court.getName());
        assertEquals("TENNIS", court.getType());
        assertEquals("Tbilisi", court.getLocation());
        assertEquals(35, court.getPricePerHour(), 0.001);
    }
}
