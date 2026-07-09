package dao;

import config.DBConnection;
import model.TrainerReview;
import model.TrainerReviewView;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrainerReviewDaoTest {
    private TrainerReviewDaoSql dao;
    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;
    private MockedStatic<DBConnection> mockedDb;

    @BeforeEach
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new TrainerReviewDaoSql();
        conn = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    public void tearDown() { if (mockedDb != null) mockedDb.close(); }

    @Test
    public void testCreateReview() throws SQLException {
        TrainerReview r = new TrainerReview();
        r.setTrainerId(1);
        r.setPlayerId(2);
        r.setRating(5);
        r.setComment("Great");

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(stmt);
        when(conn.prepareStatement(contains("UPDATE trainers"))).thenReturn(mock(PreparedStatement.class));
        when(stmt.getGeneratedKeys()).thenReturn(rs);

        when(stmt.executeUpdate()).thenReturn(1, 0);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(77);

        assertEquals(77, dao.createReview(r));
        assertEquals(-1, dao.createReview(r));

        reset(conn);
        when(conn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());
        assertEquals(-1, dao.createReview(r));
    }

    @Test
    public void testGetReviewsForTrainer() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);

        when(rs.getInt(anyString())).thenReturn(1);
        when(rs.getString(anyString())).thenReturn("Test");
        when(rs.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<TrainerReviewView> list = dao.getReviewsForTrainer(1);
        assertEquals(1, list.size());

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertTrue(dao.getReviewsForTrainer(1).isEmpty());
    }

    @Test
    public void testTrainerHasReviewFromPlayer() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        assertTrue(dao.trainerHasReviewFromPlayer(1, 2));
        assertFalse(dao.trainerHasReviewFromPlayer(1, 2));

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertFalse(dao.trainerHasReviewFromPlayer(1, 2));
    }
}