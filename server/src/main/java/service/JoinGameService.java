package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import model.JoinGameRequest;
import model.*;

public class JoinGameService {
    private final DataAccess dataAccess;
    public JoinGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void joinGameService(String authToken, JoinGameRequest request) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        String username = auth.username();
        String color = request.playerColor();

        if ("WHITE".equalsIgnoreCase(color)) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = game.withWhiteUsername(username);
        }
        else if ("BLACK".equalsIgnoreCase(color)) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = game.withBlackUsername(username);
        }
        else {
            throw new DataAccessException("Error: invalid player color");
        }
        dataAccess.updateGame(game);
    }
}
