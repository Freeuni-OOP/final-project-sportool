package service;

import dao.UserDao;
import dao.UserDaoSql;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private final UserDao userDao;

    public LoginService() {
        this.userDao = new UserDaoSql();
    }

    public LoginService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String email, String password) {
        User foundUser = userDao.getUserByEmail(email);
        if (foundUser == null) return null;
        if (!BCrypt.checkpw(password, foundUser.getPasswordHash())) return null;
        return foundUser;
    }
}