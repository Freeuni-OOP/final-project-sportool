package model;

import java.time.LocalDateTime;

public class UserBookingView {
    private int id;
    private int courtId;
    private String courtName;
    private String location;
    private String courtType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;
    private String status;
    private String paymentStatus;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourtId() { return courtId; }
    public void setCourtId(int courtId) { this.courtId = courtId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCourtType() { return courtType; }
    public void setCourtType(String courtType) { this.courtType = courtType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
