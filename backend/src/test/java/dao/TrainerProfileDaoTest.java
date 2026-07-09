package dao;

import config.DBConnection;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TrainerProfileDaoTest {
    private TrainerProfileDaoSql dao;
    private Connection conn;
    private PreparedStatement stmt;
    private ResultSet rs;
    private MockedStatic<DBConnection> mockedDb;

    @BeforeEach
    public void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        dao = new TrainerProfileDaoSql();
        conn = mock(Connection.class);
        stmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    public void tearDown() { if (mockedDb != null) mockedDb.close(); }

    @Test
    public void testGetDescription() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getString("description")).thenReturn("Yoga Coach");
        assertEquals("Yoga Coach", dao.getDescription(1));
        assertNull(dao.getDescription(1));

        when(stmt.executeQuery()).thenThrow(new SQLException());
        assertNull(dao.getDescription(1));
    }

    @Test
    public void testUpsertDescription() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1, 0).thenThrow(new SQLException());

        assertTrue(dao.upsertDescription(1, "Bio"));
        assertFalse(dao.upsertDescription(1, "Bio"));
        assertFalse(dao.upsertDescription(1, "Bio"));
    }
}