package model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private int courtId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;
    private String status;

    public Booking() {}

    public Booking(int id, int userId, int courtId, LocalDateTime startTime, LocalDateTime endTime, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.courtId = courtId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourtId() { return courtId; }
    public void setCourtId(int courtId) { this.courtId = courtId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}