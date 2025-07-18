package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import java.util.Collection;
public class ListGamesService {
    private final DataAccess dataAccess;
    public ListGamesService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public Collection<GameData> listGameService(String authToken) throws  DataAccessException {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return dataAccess.listGames();
    }
}
