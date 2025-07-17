package dataaccess;
import model.*;

import java.util.Collection;


public interface DataAccess {
    // Deleting Stuff
    void clear_all() throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    // Creation Ones
    void createUser(UserData user) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;



    // Getting Data Ones
    UserData getUser(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    GameData getGame(int game_id) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;
}