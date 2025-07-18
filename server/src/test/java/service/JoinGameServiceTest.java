package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {

    private JoinGameService joinGameService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Use the real or in-memory implementation
        dataAccess = new DataManager();
        joinGameService = new JoinGameService(dataAccess);

        // Clear DB and add test auth and game
        dataAccess.clear_all();

        dataAccess.createAuth(new AuthData("token123", "user1"));
        dataAccess.createGame(new GameData(2, null, null, "Test Game", null));
    }

    @Test
    public void testJoinGameSuccess() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest("WHITE", 2);

        // Should not throw
        joinGameService.joinGameService("token123", request);

        GameData updated = dataAccess.getGame(2);
        assertEquals("user1", updated.whiteUsername());
    }

    @Test
    public void testJoinGameAlreadyTaken() throws DataAccessException {
        // Set white player to already taken
        dataAccess.updateGame(new GameData(2, "someoneElse", null, "Test Game", null));

        JoinGameRequest request = new JoinGameRequest("WHITE", 2);

        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                joinGameService.joinGameService("token123", request)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("already taken"));
    }
}
