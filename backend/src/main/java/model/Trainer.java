package model;

public class Trainer {
    private int id;
    private int userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String sportType;
    private double pricePerSession;
    private double rating;
    private int reviewCount;

    public Trainer() {}

    public Trainer(int id, int userId, String firstName, String lastName,
                   String phone, String sportType, double pricePerSession,
                   double rating, int reviewCount) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.sportType = sportType;
        this.pricePerSession = pricePerSession;
        this.rating = rating;
        this.reviewCount = reviewCount;
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

}
