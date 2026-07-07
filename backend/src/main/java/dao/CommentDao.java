package dao;

import model.Comment;

import java.util.List;

public interface CommentDao {
    public boolean createComment(Comment comment);
    public List<Comment> getCommentsByPostId(int postId);
    public boolean deleteComment(int commentId, int userId);
}
