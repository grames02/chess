package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {

    private RegisterService registerService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new DataManager();
        registerService = new RegisterService(dataAccess);
        dataAccess.clear_all();
    }

    @Test
    public void testRegisterSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("user1", "password", "user1@example.com");
        AuthData auth = registerService.register(request);

        assertNotNull(auth);
        assertEquals("user1", auth.username());

        AuthData fromDB = dataAccess.getAuth(auth.authToken());
        assertEquals("user1", fromDB.username());
    }

    @Test
    public void testRegisterDuplicateUsername() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("user1", "password", "user1@example.com");
        registerService.register(request); // First registration

        // Try registering same username again
        RegisterRequest duplicate = new RegisterRequest("user1", "password", "user1@example.com");

        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                registerService.register(duplicate)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("already"));
    }
}
