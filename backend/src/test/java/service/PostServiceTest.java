package service;

import dao.PostDao;
import model.Post;
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

public class PostServiceTest {
    @Mock
    private PostDao mockPostDao;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);

        Field field = PostService.class.getDeclaredField("postDao");
        field.setAccessible(true);
        field.set(postService, mockPostDao);
    }

    @Test
    public void testCreatePostSuccess() {
        Post validPost = new Post(0, 10, "New Post", "hello my friends", null);

        when(mockPostDao.createPost(validPost)).thenReturn(42);
        int result = postService.createPost(validPost);
        assertEquals(42, result);
    }

    @Test
    public void testCreatePostFailEmptyTitle() {
        Post invalidPost = new Post(0, 10, "", "text", null);

        int result = postService.createPost(invalidPost);
        assertEquals(-1, result);
        verify(mockPostDao, never()).createPost(any(Post.class));
    }

    @Test
    public void testCreatePostFailNullContent() {
        Post invalidPost = new Post(0, 10, "title", null, null);

        int result = postService.createPost(invalidPost);
        assertEquals(-1, result);
        verify(mockPostDao, never()).createPost(any(Post.class));
    }

    @Test
    public void testCreatePostFailInvalidUserId() {
        Post invalidPost = new Post(0, 0, "title", "text", null);

        int result = postService.createPost(invalidPost);
        assertEquals(-1, result);
        verify(mockPostDao, never()).createPost(any(Post.class));
    }

    @Test
    public void testGetAllPostsSuccess() {
        List<Post> mockList = new ArrayList<>();
        mockList.add(new Post(1, 10, "Title", "Content", null));

        when(mockPostDao.getAllPosts()).thenReturn(mockList);

        List<Post> result = postService.getAllPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
        verify(mockPostDao).getAllPosts();
    }

    @Test
    public void testGetPostById() {
        Post mockPost = new Post(1, 10, "Title", "Content", null);
        when(mockPostDao.getPostById(1)).thenReturn(mockPost);

        Post result = postService.getPostById(1);

        assertNotNull(result);
        assertEquals("Title", result.getTitle());
    }

    @Test
    public void testDeletePost() {
        when(mockPostDao.deletePost(1)).thenReturn(true);

        boolean result = postService.deletePost(1);

        assertTrue(result);
        verify(mockPostDao).deletePost(1);
    }
}
