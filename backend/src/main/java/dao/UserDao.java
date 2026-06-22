package dao;

import model.User;

public interface UserDao {
    String registerUser(User user_);

    User getUserByEmail(String email);
}