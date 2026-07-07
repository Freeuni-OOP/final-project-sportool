package service;

import dao.CommentDao;
import dao.CommentDaoSql;
import model.Comment;
import java.util.List;

public class CommentService {
    private final CommentDao commentDao;

    public CommentService() {
        this.commentDao = new CommentDaoSql();
    }

    public List<Comment> getCommentsForPost(int postId) {
        return commentDao.getCommentsByPostId(postId);
    }

    public boolean addComment(Comment comment) {
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return false;
        }
        return commentDao.createComment(comment);
    }

    public boolean removeComment(int commentId, int userId) {
        return commentDao.deleteComment(commentId, userId);
    }
}