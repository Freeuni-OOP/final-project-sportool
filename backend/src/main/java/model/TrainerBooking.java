package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrainerBooking {
    private int id;
    private int trainerId;
    private int trainerVenueId;
    private int playerId;
    private LocalDate requestedDate;
    private String requestedTimeSlot;
    private String status;
    private LocalDateTime createdAt;

    public TrainerBooking() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public int getTrainerVenueId() { return trainerVenueId; }
    public void setTrainerVenueId(int trainerVenueId) { this.trainerVenueId = trainerVenueId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }
    public String getRequestedTimeSlot() { return requestedTimeSlot; }
    public void setRequestedTimeSlot(String requestedTimeSlot) { this.requestedTimeSlot = requestedTimeSlot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
