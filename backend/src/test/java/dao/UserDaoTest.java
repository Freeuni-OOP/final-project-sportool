package dao;

import config.DBConnection;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDaoTest {

    private UserDaoSql dao;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBConnection> mockedDbConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new UserDaoSql();

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
    public void testRegisterUserSuccess() throws SQLException {
        User u = new User(0, "test@freeuni.edu.ge", "password123", "PLAYER", "Giorgi");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        boolean result = dao.registerUser(u);

        assertTrue(result);
        verify(mockStatement).setString(1, u.getEmail());
        verify(mockStatement).setString(3, u.getRole());
        verify(mockStatement).setString(4, u.getFullName());
    }

    @Test
    public void testRegisterUserFailure() throws SQLException {
        User u = new User(0, "test@freeuni.edu.ge", "password123", "PLAYER", "Giorgi");

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        boolean result = dao.registerUser(u);

        assertFalse(result);
    }

    @Test
    public void testGetUserByEmailSuccess() throws SQLException {
        String email = "findme@freeuni.edu.ge";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(7);
        when(mockResultSet.getString("email")).thenReturn(email);
        when(mockResultSet.getString("password_hash")).thenReturn("hashed_pass");
        when(mockResultSet.getString("role")).thenReturn("COACH");
        when(mockResultSet.getString("full_name")).thenReturn("Luka Luka");

        User foundUser = dao.getUserByEmail(email);

        assertNotNull(foundUser);
        assertEquals(7, foundUser.getId());
        assertEquals(email, foundUser.getEmail());
        assertEquals("COACH", foundUser.getRole());
        assertEquals("Luka Luka", foundUser.getFullName());
    }

    @Test
    public void testGetUserByEmailNotFound() throws SQLException {
        String email = "notfound@freeuni.edu.ge";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);

        User foundUser = dao.getUserByEmail(email);

        assertNull(foundUser);
    }
}