package service;

import dao.PostDao;
import dao.PostDaoSql;
import model.Post;

import java.util.List;

public class PostService {
    private final PostDao postDao;

    public PostService() {
        this.postDao = new PostDaoSql();
    }

    public int createPost(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty() ||
                post.getContent() == null || post.getContent().trim().isEmpty()) {
            return -1;
        }
        if (post.getUserId() <= 0) {
            return -1;
        }
        return postDao.createPost(post);
    }

    public List<Post> getAllPosts() {
        return postDao.getAllPosts();
    }

    public Post getPostById(int id) {
        return postDao.getPostById(id);
    }

    public boolean deletePost(int id) {
        return postDao.deletePost(id);
    }
}
