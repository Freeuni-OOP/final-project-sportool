package service;

import dao.MatchAnnouncementDao;
import model.MatchAnnouncement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchAnnouncementServiceTest {
    private MatchAnnouncementDao mockMatchAnnouncementDao;
    private MatchAnnouncementService matchAnnouncementService;

    @BeforeEach
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        mockMatchAnnouncementDao = mock(MatchAnnouncementDao.class);
        matchAnnouncementService = new MatchAnnouncementService(mockMatchAnnouncementDao);
    }

    @Test
    public void testCreateMatchSuccessTrimsFields() {
        MatchAnnouncement match = sampleMatch();
        match.setVenue("  Saburtalo Football Arena  ");
        match.setSportType("  Football  ");
        match.setNotes("  Bring dark shirts  ");

        when(mockMatchAnnouncementDao.createMatch(match)).thenReturn(15);

        int result = matchAnnouncementService.createMatch(match);

        assertEquals(15, result);
        assertEquals("Saburtalo Football Arena", match.getVenue());
        assertEquals("Football", match.getSportType());
        assertEquals("Bring dark shirts", match.getNotes());
        verify(mockMatchAnnouncementDao).createMatch(match);
    }

    @Test
    public void testCreateMatchRejectsInvalidInput() {
        MatchAnnouncement match = sampleMatch();
        match.setPlayersNeeded(0);

        int result = matchAnnouncementService.createMatch(match);

        assertEquals(-1, result);
        verify(mockMatchAnnouncementDao, never()).createMatch(any(MatchAnnouncement.class));
    }

    @Test
    public void testGetAllMatches() {
        MatchAnnouncement match = sampleMatch();
        when(mockMatchAnnouncementDao.getAllMatches(5)).thenReturn(List.of(match));

        List<MatchAnnouncement> result = matchAnnouncementService.getAllMatches(5);

        assertEquals(1, result.size());
        assertEquals(match, result.get(0));
        verify(mockMatchAnnouncementDao).getAllMatches(5);
    }

    @Test
    public void testGetMatchByIdRejectsInvalidId() {
        MatchAnnouncement result = matchAnnouncementService.getMatchById(0);

        assertNull(result);
        verify(mockMatchAnnouncementDao, never()).getMatchById(anyInt());
    }

    @Test
    public void testUpdateMatchRequiresCreator() {
        MatchAnnouncement existing = sampleMatch();
        MatchAnnouncement update = sampleMatch();
        update.setVenue("Updated Venue");
        when(mockMatchAnnouncementDao.getMatchById(update.getId())).thenReturn(existing);
        when(mockMatchAnnouncementDao.updateMatch(update)).thenReturn(true);

        boolean result = matchAnnouncementService.updateMatch(update, existing.getUserId());

        assertTrue(result);
        verify(mockMatchAnnouncementDao).updateMatch(update);
    }

    @Test
    public void testUpdateMatchRejectsWrongUser() {
        MatchAnnouncement existing = sampleMatch();
        MatchAnnouncement update = sampleMatch();
        when(mockMatchAnnouncementDao.getMatchById(update.getId())).thenReturn(existing);

        boolean result = matchAnnouncementService.updateMatch(update, 999);

        assertFalse(result);
        verify(mockMatchAnnouncementDao, never()).updateMatch(any(MatchAnnouncement.class));
    }

    @Test
    public void testDeleteMatchRequiresCreator() {
        MatchAnnouncement match = sampleMatch();
        when(mockMatchAnnouncementDao.getMatchById(match.getId())).thenReturn(match);
        when(mockMatchAnnouncementDao.deleteMatch(match.getId())).thenReturn(true);

        boolean result = matchAnnouncementService.deleteMatch(match.getId(), match.getUserId());

        assertTrue(result);
        verify(mockMatchAnnouncementDao).deleteMatch(match.getId());
    }

    @Test
    public void testDeleteMatchRejectsWrongUser() {
        MatchAnnouncement match = sampleMatch();
        when(mockMatchAnnouncementDao.getMatchById(match.getId())).thenReturn(match);

        boolean result = matchAnnouncementService.deleteMatch(match.getId(), 999);

        assertFalse(result);
        verify(mockMatchAnnouncementDao, never()).deleteMatch(anyInt());
    }

    @Test
    public void testDeleteMatchRejectsNotFound() {
        when(mockMatchAnnouncementDao.getMatchById(404)).thenReturn(null);

        boolean result = matchAnnouncementService.deleteMatch(404, 5);

        assertFalse(result);
        verify(mockMatchAnnouncementDao, never()).deleteMatch(anyInt());
    }

    @Test
    public void testJoinMatchSuccess() {
        MatchAnnouncement match = sampleMatch();
        when(mockMatchAnnouncementDao.getMatchById(match.getId())).thenReturn(match);
        when(mockMatchAnnouncementDao.joinMatch(match.getId(), 8)).thenReturn(true);

        boolean result = matchAnnouncementService.joinMatch(match.getId(), 8);

        assertTrue(result);
        verify(mockMatchAnnouncementDao).joinMatch(match.getId(), 8);
    }

    @Test
    public void testJoinMatchRejectsCreatorSelfJoin() {
        MatchAnnouncement match = sampleMatch();
        when(mockMatchAnnouncementDao.getMatchById(match.getId())).thenReturn(match);

        boolean result = matchAnnouncementService.joinMatch(match.getId(), match.getUserId());

        assertFalse(result);
        verify(mockMatchAnnouncementDao, never()).joinMatch(anyInt(), anyInt());
    }

    @Test
    public void testJoinMatchRejectsFullMatch() {
        MatchAnnouncement match = sampleMatch();
        match.setPlayersNeeded(0);
        when(mockMatchAnnouncementDao.getMatchById(match.getId())).thenReturn(match);

        boolean result = matchAnnouncementService.joinMatch(match.getId(), 8);

        assertFalse(result);
        verify(mockMatchAnnouncementDao, never()).joinMatch(anyInt(), anyInt());
    }

    @Test
    public void testJoinMatchRejectsNotFound() {
        when(mockMatchAnnouncementDao.getMatchById(404)).thenReturn(null);

        boolean result = matchAnnouncementService.joinMatch(404, 8);

        assertFalse(result);
        verify(mockMatchAnnouncementDao, never()).joinMatch(anyInt(), anyInt());
    }

    private MatchAnnouncement sampleMatch() {
        return new MatchAnnouncement(
                10,
                5,
                "Saburtalo Football Arena",
                Timestamp.valueOf("2026-07-10 20:30:00"),
                "Football",
                3,
                "Bring dark shirts",
                "Intermediate",
                "555-123-456",
                Timestamp.valueOf("2026-07-08 12:00:00")
        );
    }
}
