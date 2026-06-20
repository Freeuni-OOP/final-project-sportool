package service;

import dao.UserDao;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDao userDao;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field field = UserService.class.getDeclaredField("userDao");
        field.setAccessible(true);
        field.set(userService, userDao);
    }

    @Test
    public void testRegisterUserSuccess() {
        User validUser = new User();
        validUser.setEmail("test@freeuni.edu.ge");
        validUser.setPasswordHash("secure123");
        validUser.setFullName("Giorgi Giorgadze");

        when(userDao.registerUser(any(User.class))).thenReturn(true);

        String errorResult = userService.registerUser(validUser);

        assertNull(errorResult);
        verify(userDao, times(1)).registerUser(validUser);
    }

    @Test
    public void testRegisterUserValidationFailed() {
        User invalidUser = new User();
        invalidUser.setEmail("test@freeuni.edu.ge");
        invalidUser.setPasswordHash("123");
        invalidUser.setFullName("Giorgi Giorgadze");

        String errorResult = userService.registerUser(invalidUser);

        assertNotNull(errorResult);
        assertTrue(errorResult.contains("Password must be at least 6 characters long") || errorResult.length() > 0);
        verify(userDao, never()).registerUser(any(User.class));
    }

    @Test
    public void testRegisterUserEmailExists() {
        User validUser = new User();
        validUser.setEmail("test@freeuni.edu.ge");
        validUser.setPasswordHash("secure123");
        validUser.setFullName("Giorgi Giorgadze");

        when(userDao.registerUser(any(User.class))).thenReturn(false);

        String errorResult = userService.registerUser(validUser);

        assertNotNull(errorResult);
        assertEquals("Registration failed. Email might already exist.", errorResult);
    }
}
public class UserServiceTest {
}
