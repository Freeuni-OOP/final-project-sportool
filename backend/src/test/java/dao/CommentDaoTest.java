package dao;

import config.DBConnection;
import model.Comment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CommentDaoTest {
    private CommentDao dao;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBConnection> mockedDbConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new CommentDaoSql();

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
    public void testCreateComment() throws SQLException {
        Comment comment = new Comment(0, 1, 10, null, "Nice court!", null);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        boolean isCreated = dao.createComment(comment);

        assertTrue(isCreated);
        verify(mockStatement).setInt(1, comment.getPostId());
        verify(mockStatement).setInt(2, comment.getUserId());
        verify(mockStatement).setString(3, comment.getContent());
    }

    @Test
    public void testGetCommentsByPostId() throws SQLException {
        int postId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);


        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("id")).thenReturn(100, 101);
        when(mockResultSet.getInt("post_id")).thenReturn(postId, postId);
        when(mockResultSet.getInt("user_id")).thenReturn(5, 6);
        when(mockResultSet.getString("full_name")).thenReturn("George", "Anna");
        when(mockResultSet.getString("content")).thenReturn("I'm in!", "Great choice");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<Comment> comments = dao.getCommentsByPostId(postId);

        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("George", comments.get(0).getUserFullName());
        assertEquals("I'm in!", comments.get(0).getContent());
        assertEquals("Anna", comments.get(1).getUserFullName());

        verify(mockStatement).setInt(1, postId);
    }

    @Test
    public void testDeleteComment() throws SQLException {
        int commentId = 100;
        int userId = 5;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        boolean isDeleted = dao.deleteComment(commentId, userId);

        assertTrue(isDeleted);
        verify(mockStatement).setInt(1, commentId);
        verify(mockStatement).setInt(2, userId);
    }
}