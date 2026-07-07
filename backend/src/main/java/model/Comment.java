package model;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private int postId;
    private int userId;
    private String userFullName;
    private String content;
    private Timestamp createdAt;

    public Comment() {}

    public Comment(int id, int postId, int userId, String userFullName, String content, Timestamp createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.userFullName = userFullName;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
