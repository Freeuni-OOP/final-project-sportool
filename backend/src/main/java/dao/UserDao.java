package dao;

import model.User;

public interface UserDao {
    boolean registerUser(User user_);

    User getUserByEmail(String email);
}