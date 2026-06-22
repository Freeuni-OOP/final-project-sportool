package dao;

import config.DBConnection;
import model.Trainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TrainerDaoTest {

    private TrainerDaoSql dao;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBConnection> mockedDbConnection;

    @BeforeEach
    void setUp() throws SQLException {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new TrainerDaoSql();

        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        mockedDbConnection = mockStatic(DBConnection.class);
        mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (mockedDbConnection != null) {
            mockedDbConnection.close();
        }
    }

    @Test
    void testAddTrainer() throws SQLException {
        Trainer trainer = new Trainer(0, 1, "Dato", "Okmelashvili",
                "599123456", "Football", 50.0, 0.0, 0);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.addTrainer(trainer));
    }

    @Test
    void testAddTrainerFails() throws SQLException {
        Trainer trainer = new Trainer(0, 1, "Dato", "Okmelashvili",
                "599123456", "Football", 50.0, 0.0, 0);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.addTrainer(trainer));
    }

    @Test
    void testAddTrainerDatabaseError() throws SQLException {
        Trainer trainer = new Trainer(0, 1, "Dato", "Okmelashvili",
                "599123456", "Football", 50.0, 0.0, 0);

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertFalse(dao.addTrainer(trainer));
    }

    @Test
    void testGetAllTrainers() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(1, 2);
        when(mockResultSet.getString("first_name")).thenReturn("Dato", "Nika");
        when(mockResultSet.getString("last_name")).thenReturn("Okmelashvili", "Dodashvili");
        when(mockResultSet.getString("phone")).thenReturn("599123456", "577123456");
        when(mockResultSet.getString("sport_type")).thenReturn("Football", "Basketball");
        when(mockResultSet.getDouble("price_per_session")).thenReturn(50.0, 40.0);
        when(mockResultSet.getDouble("rating")).thenReturn(4.5, 3.8);
        when(mockResultSet.getInt("review_count")).thenReturn(10, 5);

        List<Trainer> trainers = dao.getAllTrainers();

        assertNotNull(trainers);
        assertEquals(2, trainers.size());
        assertEquals("Dato", trainers.get(0).getFirstName());
        assertEquals("Nika", trainers.get(1).getFirstName());
    }

    @Test
    void testGetAllTrainersDatabaseError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        List<Trainer> trainers = dao.getAllTrainers();

        assertNotNull(trainers);
        assertEquals(0, trainers.size());
    }

    @Test
    void testGetTrainerById() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getString("first_name")).thenReturn("Dato");
        when(mockResultSet.getString("last_name")).thenReturn("Okmelashvili");
        when(mockResultSet.getString("phone")).thenReturn("599123456");
        when(mockResultSet.getString("sport_type")).thenReturn("Football");
        when(mockResultSet.getDouble("price_per_session")).thenReturn(50.0);
        when(mockResultSet.getDouble("rating")).thenReturn(4.5);
        when(mockResultSet.getInt("review_count")).thenReturn(10);

        Trainer result = dao.getTrainerById(1);

        assertNotNull(result);
        assertEquals("Dato", result.getFirstName());
    }

    @Test
    void testGetTrainerByIdNotFound() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Trainer result = dao.getTrainerById(999);

        assertNull(result);
    }

    @Test
    void testGetTrainerByIdDatabaseError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        Trainer result = dao.getTrainerById(1);

        assertNull(result);
    }

    @Test
    void testUpdateRating() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        assertTrue(dao.updateRating(1, 4.5, 11));
    }

    @Test
    void testUpdateRatingFails() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);

        assertFalse(dao.updateRating(1, 4.5, 11));
    }

    @Test
    void testUpdateRatingDatabaseError() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertFalse(dao.updateRating(1, 4.5, 11));
    }
}