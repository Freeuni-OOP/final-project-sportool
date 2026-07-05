package dao;

import model.Post;

import java.util.List;

public interface PostDao {
    int createPost(Post post);
    List<Post> getAllPosts();
    Post getPostById(int id);
    boolean deletePost(int id);
}
