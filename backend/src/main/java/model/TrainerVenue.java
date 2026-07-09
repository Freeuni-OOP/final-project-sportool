package model;

import java.util.ArrayList;
import java.util.List;

public class TrainerVenue {
    private int id;
    private int trainerId;
    private String venueName;
    private String address;
    private Double priceOverride;
    private List<AvailabilitySlot> availability = new ArrayList<>();

    public TrainerVenue() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getPriceOverride() { return priceOverride; }
    public void setPriceOverride(Double priceOverride) { this.priceOverride = priceOverride; }
    public List<AvailabilitySlot> getAvailability() { return availability; }
    public void setAvailability(List<AvailabilitySlot> availability) {
        this.availability = availability != null ? availability : new ArrayList<>();
    }
}
