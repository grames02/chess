package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccess;

public class JoinGameService {
    private final DataAccess dataAccess;
    public JoinGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void joinGameService() throws DataAccessException {
        dataAccess.join_game();
    }
}
