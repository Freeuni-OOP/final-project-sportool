package service;

import dao.MatchAnnouncementDao;
import dao.MatchAnnouncementDaoSql;
import model.MatchAnnouncement;

import java.util.List;

public class MatchAnnouncementService {
    private final MatchAnnouncementDao matchAnnouncementDao;

    public MatchAnnouncementService() {
        this.matchAnnouncementDao = new MatchAnnouncementDaoSql();
    }

    public MatchAnnouncementService(MatchAnnouncementDao matchAnnouncementDao) {
        this.matchAnnouncementDao = matchAnnouncementDao;
    }

    public int createMatch(MatchAnnouncement match) {
        if (match.getUserId() <= 0) return -1;
        if (match.getVenue() == null || match.getVenue().trim().isEmpty()) return -1;
        if (match.getMatchTime() == null) return -1;
        if (match.getSportType() == null || match.getSportType().trim().isEmpty()) return -1;
        if (match.getPlayersNeeded() <= 0) return -1;

        match.setVenue(match.getVenue().trim());
        match.setSportType(match.getSportType().trim());
        match.setNotes(cleanOptional(match.getNotes()));
        match.setSkillLevel(cleanOptional(match.getSkillLevel()));
        match.setContactInfo(cleanOptional(match.getContactInfo()));

        return matchAnnouncementDao.createMatch(match);
    }

    public List<MatchAnnouncement> getAllMatches(int currentUserId) {
        return matchAnnouncementDao.getAllMatches(currentUserId);
    }

    public MatchAnnouncement getMatchById(int id) {
        if (id <= 0) {
            return null;
        }
        return matchAnnouncementDao.getMatchById(id);
    }

    public boolean updateMatch(MatchAnnouncement match, int userId) {
        if (match == null || match.getId() <= 0 || userId <= 0) {
            return false;
        }

        MatchAnnouncement existingMatch = matchAnnouncementDao.getMatchById(match.getId());
        if (existingMatch == null || existingMatch.getUserId() != userId) {
            return false;
        }

        if (match.getVenue() == null || match.getVenue().trim().isEmpty()) return false;
        if (match.getMatchTime() == null) return false;
        if (match.getSportType() == null || match.getSportType().trim().isEmpty()) return false;
        if (match.getPlayersNeeded() < 0) return false;

        match.setVenue(match.getVenue().trim());
        match.setSportType(match.getSportType().trim());
        match.setNotes(cleanOptional(match.getNotes()));
        match.setSkillLevel(cleanOptional(match.getSkillLevel()));
        match.setContactInfo(cleanOptional(match.getContactInfo()));

        return matchAnnouncementDao.updateMatch(match);
    }

    public boolean deleteMatch(int matchId, int userId) {
        if (matchId <= 0 || userId <= 0) {
            return false;
        }

        MatchAnnouncement match = matchAnnouncementDao.getMatchById(matchId);
        if (match == null || match.getUserId() != userId) {
            return false;
        }

        return matchAnnouncementDao.deleteMatch(matchId);
    }

    public boolean joinMatch(int matchId, int userId) {
        if (matchId <= 0 || userId <= 0) {
            return false;
        }

        MatchAnnouncement match = matchAnnouncementDao.getMatchById(matchId);
        if (match == null || match.getPlayersNeeded() <= 0 || match.getUserId() == userId) {
            return false;
        }

        return matchAnnouncementDao.joinMatch(matchId, userId);
    }

    private String cleanOptional(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
