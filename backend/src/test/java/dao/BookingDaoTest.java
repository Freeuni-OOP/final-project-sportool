package dao;

import config.DBConnection;
import model.Booking;
import model.UserBookingView;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.sql.*;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BookingDaoTest {
    private BookingDaoSql dao;
    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;
    private MockedStatic<DBConnection> mockedDb;

    @BeforeEach
    public void setUp() throws SQLException {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new BookingDaoSql();
        conn = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    public void tearDown() { if (mockedDb != null) mockedDb.close(); }

    @Test
    public void testCreateBooking() throws SQLException {
        Booking b = new Booking(0, 1, 2, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 50.0, null);
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(stmt);

        when(stmt.executeUpdate()).thenReturn(1, 0).thenThrow(new SQLException());
        when(stmt.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(99);

        assertTrue(dao.createBooking(b));
        assertEquals(99, b.getId());
        assertFalse(dao.createBooking(b));
        assertFalse(dao.createBooking(b));
    }

    @Test
    public void testIsCourtAvailable() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt(1)).thenReturn(0, 3);
        assertTrue(dao.isCourtAvailable(1, LocalDateTime.now(), LocalDateTime.now()));
        assertFalse(dao.isCourtAvailable(1, LocalDateTime.now(), LocalDateTime.now()));

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertFalse(dao.isCourtAvailable(1, LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void testGetBookingsForCourtOnDate() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);

        when(rs.getTimestamp(anyString())).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getString(anyString())).thenReturn("CONFIRMED");

        List<Booking> list = dao.getBookingsForCourtOnDate(1, LocalDate.now());
        assertEquals(1, list.size());

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertTrue(dao.getBookingsForCourtOnDate(1, LocalDate.now()).isEmpty());
    }

    @Test
    public void testGetBookingsByUserId() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);

        when(rs.getTimestamp(anyString())).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getString("payment_status")).thenReturn(null);

        List<UserBookingView> list = dao.getBookingsByUserId(10);
        assertEquals(1, list.size());
        assertEquals("PAID", list.get(0).getPaymentStatus());

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertTrue(dao.getBookingsByUserId(10).isEmpty());
    }
}