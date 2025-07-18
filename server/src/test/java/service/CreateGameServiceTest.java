package service;

import dataaccess.*;
import model.AuthData;
import model.CreateGameRequest;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {

    private CreateGameService createGameService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new DataManager(); // Your in-memory DAO
        createGameService = new CreateGameService(dataAccess);

        // Add a valid auth token
        dataAccess.createAuth(new AuthData("validToken", "testUser"));
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("MyGame");
        GameData createdGame = createGameService.createGame(request, "validToken");

        assertNotNull(createdGame);
        assertEquals("MyGame", createdGame.gameName());
    }

    @Test
    public void testCreateGameInvalidAuth() {
        CreateGameRequest request = new CreateGameRequest("MyGame");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(request, "invalidToken");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }
}
