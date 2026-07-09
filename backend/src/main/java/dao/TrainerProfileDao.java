package dao;

public interface TrainerProfileDao {
    String getDescription(int trainerId);
    boolean upsertDescription(int trainerId, String description);
}

