package dao;

import model.user;

public interface userDao {
    boolean registerUser(user user_);

    user getUserByEmail(String email);
}