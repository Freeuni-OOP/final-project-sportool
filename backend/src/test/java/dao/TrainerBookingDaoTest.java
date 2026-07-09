package dao;

import config.DBConnection;
import model.TrainerBooking;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.sql.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrainerBookingDaoTest {
    private TrainerBookingDaoSql dao;
    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;
    private MockedStatic<DBConnection> mockedDb;

    @BeforeEach
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        dao = new TrainerBookingDaoSql();
        conn = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    public void tearDown() { if (mockedDb != null) mockedDb.close(); }

    @Test
    public void testCreateAndUpdate() throws SQLException {
        TrainerBooking b = new TrainerBooking();
        b.setRequestedDate(LocalDate.now());

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1, 0);
        when(stmt.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(42);

        assertEquals(42, dao.createBooking(b));
        assertEquals(-1, dao.createBooking(b));

        PreparedStatement stmtUpd = mock(PreparedStatement.class);
        when(conn.prepareStatement(contains("UPDATE"))).thenReturn(stmtUpd);
        when(stmtUpd.executeUpdate()).thenReturn(1, 0).thenThrow(new SQLException());

        assertTrue(dao.updateStatus(1, 2, "OK"));
        assertFalse(dao.updateStatus(1, 2, "OK"));
        assertFalse(dao.updateStatus(1, 2, "OK"));
    }

    @Test
    public void testQueries() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false, true, false, true, false, false);
        when(rs.wasNull()).thenReturn(false);

        assertEquals(1, dao.getBookingsByPlayerId(1).size());
        assertEquals(1, dao.getBookingsByTrainerId(1).size());
        assertNotNull(dao.getBookingById(1));
        assertNull(dao.getBookingById(2));
    }
}