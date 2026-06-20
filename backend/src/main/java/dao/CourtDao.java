package dao;

import model.Court;

import java.util.List;

public interface CourtDao {
    public List<Court> getCourtsByType(String type);
    public List<Court> getAllCourts();
}