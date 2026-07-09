package model;

import java.time.LocalDateTime;

public class TrainerReview {
    private int id;
    private int trainerId;
    private int playerId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public TrainerReview() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

