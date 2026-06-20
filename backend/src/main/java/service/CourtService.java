package service;

import dao.CourtDaoSql;
import model.Court;

import java.util.List;

public class CourtService {
    private final CourtDaoSql courtDao;

    public CourtService() {
        this.courtDao = new CourtDaoSql();
    }

    public List<Court> getCourts(String type) {
        if (type == null || type.trim().isEmpty() || type.equalsIgnoreCase("ALL")) {
            return courtDao.getAllCourts();
        }
        return courtDao.getCourtsByType(type);
    }
}
