package dao;

import config.DBConnection;
import model.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PostDaoTest {
    private PostDaoSql dao;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBConnection> mockedDbConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        System.setProperty("net.bytebuddy.experimental", "true");

        dao = new PostDaoSql();

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
    public void testCreatePostSuccess() throws SQLException {
        Post post = new Post(0, 10, "title", "text", null);
        ResultSet mockGeneratedKeys = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(42);

        int postId = dao.createPost(post);

        assertEquals(42, postId);
        verify(mockStatement).setInt(1, post.getUserId());
        verify(mockStatement).setString(2, post.getTitle());
        verify(mockStatement).setString(3, post.getContent());
    }

    @Test
    public void testGetAllPostsSuccess() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(10, 11);
        when(mockResultSet.getString("title")).thenReturn("Title 1", "Title 2");
        when(mockResultSet.getString("content")).thenReturn("Text 1", "Text 2");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<Post> posts = dao.getAllPosts();

        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertEquals("Title 1", posts.get(0).getTitle());
    }

    @Test
    public void testDeletePostSuccess() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        boolean isDeleted = dao.deletePost(1);

        assertTrue(isDeleted);
    }
    @Test
    public void testGetPostByIdSuccess() throws SQLException {
        int targetId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("id")).thenReturn(targetId);
        when(mockResultSet.getInt("user_id")).thenReturn(10);
        when(mockResultSet.getString("title")).thenReturn("Title");
        when(mockResultSet.getString("content")).thenReturn("Text");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));

        Post post = dao.getPostById(targetId);

        assertNotNull(post);
        assertEquals(targetId, post.getId());
        assertEquals("Title", post.getTitle());
        verify(mockStatement).setInt(1, targetId);
    }
}
