package dao;

import config.DBConnection;
import model.Court;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class courtDaoTest {

    private courtDaoSql dao;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBConnection> mockedDbConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new courtDaoSql();

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
    public void testGetAllCourtsSuccess() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("court_name")).thenReturn("Maracana", "Wimbledon");
        when(mockResultSet.getString("court_type")).thenReturn("FOOTBALL", "TENNIS");
        when(mockResultSet.getString("location")).thenReturn("Tbilisi", "Batumi");
        when(mockResultSet.getDouble("price_per_hour")).thenReturn(50.0, 40.0);

        List<Court> courts = dao.getAllCourts();

        assertNotNull(courts);
        assertEquals(2, courts.size());
        assertEquals("Maracana", courts.get(0).getName());
        assertEquals("FOOTBALL", courts.get(0).getType());
    }

    @Test
    public void testGetCourtsByTypeSuccess() throws SQLException {
        String sportType = "PADEL";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(3);
        when(mockResultSet.getString("court_name")).thenReturn("Padel Central");
        when(mockResultSet.getString("court_type")).thenReturn(sportType);
        when(mockResultSet.getString("location")).thenReturn("Tbilisi");
        when(mockResultSet.getDouble("price_per_hour")).thenReturn(60.0);

        List<Court> courts = dao.getCourtsByType(sportType);

        assertNotNull(courts);
        assertEquals(1, courts.size());
        assertEquals("Padel Central", courts.get(0).getName());
    }

    @Test
    public void testGetAllCourtsDatabaseError() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database connection lost"));

        List<Court> courts = dao.getAllCourts();

        assertNotNull(courts);
    }

}
