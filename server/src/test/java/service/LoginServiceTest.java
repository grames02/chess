package service;

import dataaccess.*;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private LoginService loginService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new DataManager(); // Your in-memory DAO
        loginService = new LoginService(dataAccess);

        // Create a test user
        UserData user = new UserData("user1", "password123", "user1@email.com");
        dataAccess.createUser(user);
    }

    @Test
    public void testLoginSuccess() throws DataAccessException {
        LoginRequest request = new LoginRequest("user1", "password123");
        LoginResult auth = loginService.loginService(request);

        assertNotNull(auth);
        assertEquals("user1", auth.username());
    }

    @Test
    public void testLoginWrongPassword() {
        LoginRequest request = new LoginRequest("user1", "wrongPassword");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            loginService.loginService(request);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }
}
