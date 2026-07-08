package model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class MatchAnnouncementTest {
    @Test
    public void testConstructorAndGetters() {
        Timestamp matchTime = Timestamp.valueOf("2026-07-10 20:30:00");
        Timestamp createdAt = Timestamp.valueOf("2026-07-08 12:00:00");

        MatchAnnouncement match = new MatchAnnouncement(
                1,
                5,
                "Saburtalo Football Arena",
                matchTime,
                "Football",
                3,
                "Bring dark shirts",
                "Intermediate",
                "555-123-456",
                createdAt
        );

        assertEquals(1, match.getId());
        assertEquals(5, match.getUserId());
        assertEquals("Saburtalo Football Arena", match.getVenue());
        assertEquals(matchTime, match.getMatchTime());
        assertEquals("Football", match.getSportType());
        assertEquals(3, match.getPlayersNeeded());
        assertEquals("Bring dark shirts", match.getNotes());
        assertEquals("Intermediate", match.getSkillLevel());
        assertEquals("555-123-456", match.getContactInfo());
        assertEquals(createdAt, match.getCreatedAt());
    }

    @Test
    public void testSetters() {
        MatchAnnouncement match = new MatchAnnouncement();
        Timestamp createdAt = Timestamp.valueOf("2026-07-08 12:00:00");

        match.setId(2);
        match.setUserId(7);
        match.setVenue("Vera Park Basketball Court");
        match.setMatchTime("2026-07-11T18:15");
        match.setSportType("Basketball");
        match.setPlayersNeeded(2);
        match.setNotes("Indoor shoes required");
        match.setSkillLevel("Mixed");
        match.setContactInfo("@sportool-player");
        match.setCreatedAt(createdAt);
        match.setJoinedByCurrentUser(true);

        assertEquals(2, match.getId());
        assertEquals(7, match.getUserId());
        assertEquals("Vera Park Basketball Court", match.getVenue());
        assertEquals(Timestamp.valueOf("2026-07-11 18:15:00"), match.getMatchTime());
        assertEquals("Basketball", match.getSportType());
        assertEquals(2, match.getPlayersNeeded());
        assertEquals("Indoor shoes required", match.getNotes());
        assertEquals("Mixed", match.getSkillLevel());
        assertEquals("@sportool-player", match.getContactInfo());
        assertEquals(createdAt, match.getCreatedAt());
        assertTrue(match.isJoinedByCurrentUser());
    }

    @Test
    public void testSetMatchTimeBlankSetsNull() {
        MatchAnnouncement match = new MatchAnnouncement();

        match.setMatchTime(" ");

        assertNull(match.getMatchTime());
    }

    @Test
    public void testSetMatchTimeInvalidFormatThrowsException() {
        MatchAnnouncement match = new MatchAnnouncement();

        assertThrows(IllegalArgumentException.class, () -> match.setMatchTime("not-a-date"));
    }
}
