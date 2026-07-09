package dao;

import config.DBConnection;
import model.AvailabilitySlot;
import model.TrainerVenue;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrainerVenueDaoTest {
    private TrainerVenueDaoSql dao;
    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;
    private MockedStatic<DBConnection> mockedDb;

    @BeforeEach
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new TrainerVenueDaoSql();
        conn = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    public void tearDown() { if (mockedDb != null) mockedDb.close(); }

    @Test
    public void testCreateVenue() throws SQLException {
        TrainerVenue v = new TrainerVenue();
        v.setPriceOverride(100.0);
        List<AvailabilitySlot> slots = new ArrayList<>();
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setDayOfWeek("MONDAY");
        slot.setStartTime("10:00");
        slot.setEndTime("11:00");
        slots.add(slot);
        v.setAvailability(slots);

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(stmt);
        when(conn.prepareStatement(contains("trainer_availability"))).thenReturn(mock(PreparedStatement.class));
        when(stmt.getGeneratedKeys()).thenReturn(rs);

        when(stmt.executeUpdate()).thenReturn(1, 0);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(5);

        assertEquals(5, dao.createVenue(v));

        v.setPriceOverride(null);
        assertEquals(-1, dao.createVenue(v));

        reset(conn);
        when(conn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());
        assertEquals(-1, dao.createVenue(v));
    }

    @Test
    public void testUpdateVenue() throws SQLException {
        TrainerVenue v = new TrainerVenue();
        v.setId(1);
        v.setPriceOverride(50.0);

        PreparedStatement stmtDel = mock(PreparedStatement.class);
        PreparedStatement stmtIns = mock(PreparedStatement.class);
        when(conn.prepareStatement(contains("UPDATE"))).thenReturn(stmt);
        when(conn.prepareStatement(contains("DELETE"))).thenReturn(stmtDel);
        when(conn.prepareStatement(contains("INSERT"))).thenReturn(stmtIns);

        when(stmt.executeUpdate()).thenReturn(1, 0);
        assertTrue(dao.updateVenue(v));
        assertFalse(dao.updateVenue(v));

        v.setPriceOverride(null);
        when(stmt.executeUpdate()).thenThrow(new SQLException());
        assertFalse(dao.updateVenue(v));
    }

    @Test
    public void testDeleteVenue() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1, 0).thenThrow(new SQLException());

        assertTrue(dao.deleteVenue(1, 2));
        assertFalse(dao.deleteVenue(1, 2));
        assertFalse(dao.deleteVenue(1, 2));
    }

    @Test
    public void testGetVenueByIdAndBelongs() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false, true, false, false);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getDouble("price_override")).thenReturn(60.0);
        when(rs.wasNull()).thenReturn(false);

        TrainerVenue venue = dao.getVenueById(1);
        assertNotNull(venue);
        assertEquals(60.0, venue.getPriceOverride());

        assertTrue(dao.venueBelongsToTrainer(1, 2));
        assertFalse(dao.venueBelongsToTrainer(1, 2));

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertNull(dao.getVenueById(1));
    }

    @Test
    public void testGetVenuesByTrainerId() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false, true, false);
        when(rs.getInt("id")).thenReturn(10);
        when(rs.getInt("trainer_venue_id")).thenReturn(10);

        List<TrainerVenue> list = dao.getVenuesByTrainerId(1);
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).getId());

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertTrue(dao.getVenuesByTrainerId(1).isEmpty());
    }
}