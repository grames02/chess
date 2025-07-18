package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {

    private ListGamesService listGamesService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new DataManager();
        listGamesService = new ListGamesService(dataAccess);

        // Add user and auth
        dataAccess.createAuth(new AuthData("validToken", "testUser"));

        // Add games
        dataAccess.createGame(new GameData(1, null, null, "Game One", null));
        dataAccess.createGame(new GameData(2, null, null, "Game Two", null));
    }

    @Test
    public void testListGamesSuccess() throws DataAccessException {
        Collection<GameData> games = listGamesService.listGameService("validToken");

        assertNotNull(games);
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesInvalidAuth() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            listGamesService.listGameService("badToken");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized"));
    }
}
