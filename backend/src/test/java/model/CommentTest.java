package model;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {

    @Test
    public void testCommentConstructorAndGetters() {
        int id = 1;
        int postId = 10;
        int userId = 5;
        String userFullName = "George Basket";
        String content = "I'm in! What time?";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());


        Comment comment = new Comment(id, postId, userId, userFullName, content, createdAt);

        assertEquals(id, comment.getId());
        assertEquals(postId, comment.getPostId());
        assertEquals(userId, comment.getUserId());
        assertEquals(userFullName, comment.getUserFullName());
        assertEquals(content, comment.getContent());
        assertEquals(createdAt, comment.getCreatedAt());
    }

    @Test
    public void testCommentSetters() {

        Comment comment = new Comment();
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        comment.setId(2);
        comment.setPostId(20);
        comment.setUserId(6);
        comment.setUserFullName("Anna Smith");
        comment.setContent("Great court choice!");
        comment.setCreatedAt(createdAt);

        assertEquals(2, comment.getId());
        assertEquals(20, comment.getPostId());
        assertEquals(6, comment.getUserId());
        assertEquals("Anna Smith", comment.getUserFullName());
        assertEquals("Great court choice!", comment.getContent());
        assertEquals(createdAt, comment.getCreatedAt());
    }
}