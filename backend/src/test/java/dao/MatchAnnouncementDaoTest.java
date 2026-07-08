package dao;

import config.DBConnection;
import model.MatchAnnouncement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MatchAnnouncementDaoTest {
    private MatchAnnouncementDaoSql dao;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBConnection> mockedDbConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new MatchAnnouncementDaoSql();
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        mockedDbConnection = mockStatic(DBConnection.class);
        mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    public void tearDown() {
        if (mockedDbConnection != null) {
            mockedDbConnection.close();
        }
    }

    @Test
    public void testCreateMatchSuccess() throws SQLException {
        MatchAnnouncement match = sampleMatch();
        ResultSet mockGeneratedKeys = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(20);

        int id = dao.createMatch(match);

        assertEquals(20, id);
        verify(mockStatement).setInt(1, match.getUserId());
        verify(mockStatement).setString(2, match.getVenue());
        verify(mockStatement).setTimestamp(3, match.getMatchTime());
        verify(mockStatement).setString(4, match.getSportType());
        verify(mockStatement).setInt(5, match.getPlayersNeeded());
    }

    @Test
    public void testGetAllMatchesSuccess() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        stubMatchRow(mockResultSet);
        when(mockResultSet.getBoolean("joined_by_current_user")).thenReturn(true);

        List<MatchAnnouncement> matches = dao.getAllMatches(5);

        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals("Saburtalo Football Arena", matches.get(0).getVenue());
        assertTrue(matches.get(0).isJoinedByCurrentUser());
        verify(mockStatement).setInt(1, 5);
    }

    @Test
    public void testGetMatchByIdSuccess() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        stubMatchRow(mockResultSet);

        MatchAnnouncement match = dao.getMatchById(10);

        assertNotNull(match);
        assertEquals(10, match.getId());
        assertEquals("Football", match.getSportType());
        verify(mockStatement).setInt(1, 10);
    }

    @Test
    public void testUpdateMatchSuccess() throws SQLException {
        MatchAnnouncement match = sampleMatch();
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        boolean updated = dao.updateMatch(match);

        assertTrue(updated);
        verify(mockStatement).setString(1, match.getVenue());
        verify(mockStatement).setTimestamp(2, match.getMatchTime());
        verify(mockStatement).setString(3, match.getSportType());
        verify(mockStatement).setInt(4, match.getPlayersNeeded());
        verify(mockStatement).setInt(8, match.getId());
    }

    @Test
    public void testDeleteMatchSuccess() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        boolean deleted = dao.deleteMatch(10);

        assertTrue(deleted);
        verify(mockStatement).setInt(1, 10);
    }

    @Test
    public void testJoinMatchSuccess() throws SQLException {
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        PreparedStatement updateStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(insertStatement, updateStatement);
        when(updateStatement.executeUpdate()).thenReturn(1);

        boolean joined = dao.joinMatch(10, 6);

        assertTrue(joined);
        verify(mockConnection).setAutoCommit(false);
        verify(insertStatement).setInt(1, 10);
        verify(insertStatement).setInt(2, 6);
        verify(updateStatement).setInt(1, 10);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testJoinMatchRollbackWhenNoOpenSpots() throws SQLException {
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        PreparedStatement updateStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(insertStatement, updateStatement);
        when(updateStatement.executeUpdate()).thenReturn(0);

        boolean joined = dao.joinMatch(10, 6);

        assertFalse(joined);
        verify(mockConnection).rollback();
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

    private void stubMatchRow(ResultSet rs) throws SQLException {
        when(rs.getInt("id")).thenReturn(10);
        when(rs.getInt("user_id")).thenReturn(5);
        when(rs.getString("venue")).thenReturn("Saburtalo Football Arena");
        when(rs.getTimestamp("match_time")).thenReturn(Timestamp.valueOf("2026-07-10 20:30:00"));
        when(rs.getString("sport_type")).thenReturn("Football");
        when(rs.getInt("players_needed")).thenReturn(3);
        when(rs.getString("notes")).thenReturn("Bring dark shirts");
        when(rs.getString("skill_level")).thenReturn("Intermediate");
        when(rs.getString("contact_info")).thenReturn("555-123-456");
        when(rs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf("2026-07-08 12:00:00"));
    }
}
