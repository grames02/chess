package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.concurrent.atomic.AtomicInteger;

public class CreateGameService {
    private final DataAccess dataAccess;
    private static final AtomicInteger GAME_COUNT = new AtomicInteger(1);
    public CreateGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        if (request == null || request.gameName() == null || request.gameName().isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }
        var auth = dataAccess.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        int gameID = GAME_COUNT.getAndIncrement();
        GameData game = new GameData(gameID, null, null, request.gameName(), null);
        dataAccess.createGame(game);
        return game;
    }
}
