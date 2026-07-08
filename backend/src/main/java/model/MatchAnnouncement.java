package model;

import java.sql.Timestamp;

public class MatchAnnouncement {
    private int id;
    private int userId;
    private String venue;
    private Timestamp matchTime;
    private String sportType;
    private int playersNeeded;
    private String notes;
    private String skillLevel;
    private String contactInfo;
    private Timestamp createdAt;
    private boolean joinedByCurrentUser;

    public MatchAnnouncement() {}

    public MatchAnnouncement(int id, int userId, String venue, Timestamp matchTime,
                             String sportType, int playersNeeded, String notes,
                             String skillLevel, String contactInfo, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.venue = venue;
        this.matchTime = matchTime;
        this.sportType = sportType;
        this.playersNeeded = playersNeeded;
        this.notes = notes;
        this.skillLevel = skillLevel;
        this.contactInfo = contactInfo;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public Timestamp getMatchTime() { return matchTime; }
    public void setMatchTime(String matchTime) {
        if (matchTime == null || matchTime.isBlank()) {
            this.matchTime = null;
            return;
        }

        String normalized = matchTime.trim().replace("T", " ");
        if (normalized.length() == 16) {
            normalized = normalized + ":00";
        }
        this.matchTime = Timestamp.valueOf(normalized);
    }
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    public int getPlayersNeeded() { return playersNeeded; }
    public void setPlayersNeeded(int playersNeeded) { this.playersNeeded = playersNeeded; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public boolean isJoinedByCurrentUser() { return joinedByCurrentUser; }
    public void setJoinedByCurrentUser(boolean joinedByCurrentUser) { this.joinedByCurrentUser = joinedByCurrentUser; }
}
