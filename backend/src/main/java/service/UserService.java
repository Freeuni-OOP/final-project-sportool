package service;

import dao.UserDao;
import dao.UserDaoSql;
import model.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

public class UserService {

    private final UserDao userDao = new UserDaoSql();
    private final Validator validator;

    public UserService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }


    public String registerUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            return violations.iterator().next().getMessage();
        }

        boolean isSaved = userDao.registerUser(user);
        if (!isSaved) {
            return "Registration failed. Email might already exist.";
        }

        return null; 
    }
}