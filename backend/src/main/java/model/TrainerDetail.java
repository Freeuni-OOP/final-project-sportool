package model;

import java.util.ArrayList;
import java.util.List;

public class TrainerDetail {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String sportType;
    private double pricePerSession;
    private double rating;
    private int reviewCount;
    private String description;
    private List<TrainerVenue> venues = new ArrayList<>();

    public TrainerDetail() {}

    public TrainerDetail(Trainer trainer) {
        this.id = trainer.getId();
        this.userId = trainer.getUserId();
        this.firstName = trainer.getFirstName();
        this.lastName = trainer.getLastName();
        this.phone = trainer.getPhone();
        this.sportType = trainer.getSportType();
        this.pricePerSession = trainer.getPricePerSession();
        this.rating = trainer.getRating();
        this.reviewCount = trainer.getReviewCount();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    public double getPricePerSession() { return pricePerSession; }
    public void setPricePerSession(double pricePerSession) { this.pricePerSession = pricePerSession; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<TrainerVenue> getVenues() { return venues; }
    public void setVenues(List<TrainerVenue> venues) {
        this.venues = venues != null ? venues : new ArrayList<>();
    }
}
