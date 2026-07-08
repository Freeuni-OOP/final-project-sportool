package dao;

import model.MatchAnnouncement;

import java.util.List;

public interface MatchAnnouncementDao {
    int createMatch(MatchAnnouncement match);
    List<MatchAnnouncement> getAllMatches(int currentUserId);
    MatchAnnouncement getMatchById(int id);
    boolean updateMatch(MatchAnnouncement match);
    boolean deleteMatch(int id);
    boolean joinMatch(int matchId, int userId);
}
