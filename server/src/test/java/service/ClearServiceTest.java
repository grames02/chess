package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService clearService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new DataManager();
        clearService = new ClearService(dataAccess);

        dataAccess.createUser(new UserData("testUser", "pass", "email@test.com"));
        dataAccess.createAuth(new AuthData("token123", "testUser"));
        dataAccess.createGame(new GameData(42, null, null, "Test Game", null));
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        dataAccess.clear_all();
        assertNull(dataAccess.getUser("testUser"));
        assertNull(dataAccess.getAuth("token123"));
        assertNull(dataAccess.getGame(42));
    }
}
