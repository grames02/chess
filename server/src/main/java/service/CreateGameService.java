package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.concurrent.atomic.AtomicInteger;

public class CreateGameService {
    private final DataAccess dataAccess;
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
        GameData game = new GameData(0, null, null, request.gameName(), null);
        return dataAccess.createGame(game);
    }
}
