package service;

import dao.UserDao;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginServiceTest {

    @Mock private UserDao mockDao;
    @InjectMocks private LoginService service;

    @BeforeEach
    void setUp() {
        System.setProperty("net.bytebuddy.experimental", "true");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructorsAndFailures() {
        assertNotNull(new LoginService());

        when(mockDao.getUserByEmail("none@gmail.com")).thenReturn(null);
        assertNull(service.login("none@gmail.com", "pass"));

        User user = mock(User.class);
        when(user.getPasswordHash()).thenReturn(BCrypt.hashpw("correct", BCrypt.gensalt()));
        when(mockDao.getUserByEmail("user@gmail.com")).thenReturn(user);

        assertNull(service.login("user@gmail.com", "wrong"));
    }

    @Test
    void testLoginSuccess() {
        User user = mock(User.class);
        when(user.getPasswordHash()).thenReturn(BCrypt.hashpw("pass123", BCrypt.gensalt()));
        when(mockDao.getUserByEmail("user@gmail.com")).thenReturn(user);

        assertEquals(user, service.login("user@gmail.com", "pass123"));
    }
}