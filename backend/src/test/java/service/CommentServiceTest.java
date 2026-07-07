package service;

import dao.CommentDao;
import model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {
    @Mock
    private CommentDao mockCommentDao;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);

        Field field = CommentService.class.getDeclaredField("commentDao");
        field.setAccessible(true);
        field.set(commentService, mockCommentDao);
    }

    @Test
    public void testAddCommentSuccess() {
        Comment validComment = new Comment(0, 1, 10, "dato", "Nice play!", null);

        when(mockCommentDao.createComment(validComment)).thenReturn(true);

        boolean result = commentService.addComment(validComment);

        assertTrue(result);
        verify(mockCommentDao).createComment(validComment);
    }

    @Test
    public void testAddCommentFailEmptyContent() {
        Comment invalidComment = new Comment(0, 1, 10, "dato", "", null);

        boolean result = commentService.addComment(invalidComment);

        assertFalse(result);
        verify(mockCommentDao, never()).createComment(any(Comment.class));
    }

    @Test
    public void testAddCommentFailNullContent() {
        Comment invalidComment = new Comment(0, 1, 10, "dato", null, null);

        boolean result = commentService.addComment(invalidComment);

        assertFalse(result);
        verify(mockCommentDao, never()).createComment(any(Comment.class));
    }

    @Test
    public void testGetCommentsForPostSuccess() {
        int postId = 1;
        List<Comment> mockList = new ArrayList<>();
        mockList.add(new Comment(100, postId, 10, "dato", "Nice play!", null));

        when(mockCommentDao.getCommentsByPostId(postId)).thenReturn(mockList);

        List<Comment> result = commentService.getCommentsForPost(postId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nice play!", result.get(0).getContent());
        verify(mockCommentDao).getCommentsByPostId(postId);
    }

    @Test
    public void testRemoveCommentSuccess() {
        int commentId = 100;
        int userId = 10;

        when(mockCommentDao.deleteComment(commentId, userId)).thenReturn(true);

        boolean result = commentService.removeComment(commentId, userId);

        assertTrue(result);
        verify(mockCommentDao).deleteComment(commentId, userId);
    }
}
