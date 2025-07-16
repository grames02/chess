package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class CreateGameService {
    private final DataAccess dataAccess;
    public CreateGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void createGameService() throws DataAccessException {
        dataAccess.create_game();
    }
}
