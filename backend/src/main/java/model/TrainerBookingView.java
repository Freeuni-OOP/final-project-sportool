package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrainerBookingView {
    private int id;
    private int trainerId;
    private int trainerVenueId;
    private int playerId;
    private String playerName;
    private String trainerName;
    private String venueName;
    private String venueAddress;
    private String sportType;
    private LocalDate requestedDate;
    private String requestedTimeSlot;
    private String status;
    private LocalDateTime createdAt;
    private Double sessionPrice;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public int getTrainerVenueId() { return trainerVenueId; }
    public void setTrainerVenueId(int trainerVenueId) { this.trainerVenueId = trainerVenueId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getVenueAddress() { return venueAddress; }
    public void setVenueAddress(String venueAddress) { this.venueAddress = venueAddress; }
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }
    public String getRequestedTimeSlot() { return requestedTimeSlot; }
    public void setRequestedTimeSlot(String requestedTimeSlot) { this.requestedTimeSlot = requestedTimeSlot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Double getSessionPrice() { return sessionPrice; }
    public void setSessionPrice(Double sessionPrice) { this.sessionPrice = sessionPrice; }
}
