package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class ListGamesService {
    private final DataAccess dataAccess;
    public ListGamesService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void listGameService() throws  DataAccessException {
        dataAccess.listGames();
    }
}
