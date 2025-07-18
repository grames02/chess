package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {

    private LogoutService logoutService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new DataManager(); // In-memory DAO
        logoutService = new LogoutService(dataAccess);

        // Create an auth token for testing
        AuthData auth = new AuthData("validToken", "testUser");
        dataAccess.createAuth(auth);
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        logoutService.logoutService("validToken");

        AuthData auth = dataAccess.getAuth("validToken");
        assertNull(auth); // Should be deleted
    }

    @Test
    public void testLogoutInvalidToken() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            logoutService.logoutService("badToken");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }
}
