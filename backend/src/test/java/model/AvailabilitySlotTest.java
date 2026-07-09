package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvailabilitySlotTest {

    @Test
    public void testAvailabilitySlotConstructorAndGetters() {
        String dayOfWeek = "Monday";
        String startTime = "09:00";
        String endTime = "10:00";

        AvailabilitySlot slot = new AvailabilitySlot(dayOfWeek, startTime, endTime);

        assertEquals(dayOfWeek, slot.getDayOfWeek());
        assertEquals(startTime, slot.getStartTime());
        assertEquals(endTime, slot.getEndTime());
    }

    @Test
    public void testAvailabilitySlotSetters() {
        AvailabilitySlot slot = new AvailabilitySlot();

        int id = 1;
        String dayOfWeek = "Wednesday";
        String startTime = "14:00";
        String endTime = "16:00";

        slot.setId(id);
        slot.setDayOfWeek(dayOfWeek);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);

        assertEquals(id, slot.getId());
        assertEquals(dayOfWeek, slot.getDayOfWeek());
        assertEquals(startTime, slot.getStartTime());
        assertEquals(endTime, slot.getEndTime());
    }
}