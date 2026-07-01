package model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostTest {
    @Test
    public void testPostConstructorAndGetters() {

        int id = 1;
        int userId = 5;
        String title = "Playing basketball";
        String content = "We're playing tonight, who's joining us?";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        Post post = new Post(id, userId, title, content, createdAt);

        assertEquals(id, post.getId());
        assertEquals(userId, post.getUserId());
        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
        assertEquals(createdAt, post.getCreatedAt());
    }

    @Test
    public void testPostSetters() {
        Post post = new Post();
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        post.setId(1);
        post.setUserId(5);
        post.setTitle("New post");
        post.setContent("text");
        post.setCreatedAt(createdAt);

        assertEquals(1, post.getId());
        assertEquals(5, post.getUserId());
        assertEquals("New post", post.getTitle());
        assertEquals("text", post.getContent());
        assertEquals(createdAt, post.getCreatedAt());
    }
}
