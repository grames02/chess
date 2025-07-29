package dataaccess;
import model.*;

import java.util.Collection;


public interface DataAccess {
    // Deleting Stuff
    void clearAll() throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    // Creation Ones
    void createUser(UserData user) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    GameData createGame(GameData game) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;



    // Getting Data Ones
    UserData getUser(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;
}