package dataaccess;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

public class DataManager implements DataAccess {
    private final Map<String, UserData> userList = new HashMap<>();
    private final Map<String, AuthData> authCodes = new HashMap<>();
    private final Map<Integer, GameData> gameList = new HashMap<>();

    // Clear App.
    public void clearAll() throws DataAccessException {
        authCodes.clear();
        gameList.clear();
        userList.clear();
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        authCodes.remove(authToken);
    }

    public void createUser(UserData user) throws DataAccessException {
        if (userList.containsKey(user.username())) {
            throw new DataAccessException("User already exists.");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData hashedUser = new UserData(user.username(), hashedPassword, user.email());
        userList.put(user.username(), hashedUser);
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        authCodes.put(auth.authToken(), auth);
    }

    public void createGame(GameData game) throws DataAccessException {
        gameList.put(game.gameID(), game);
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (!gameList.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found.");
        }
        gameList.put(game.gameID(), game);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userList.get(username);
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authCodes.get(authToken);
    }
    public GameData getGame(int gameId) throws DataAccessException {
        return gameList.get(gameId);
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return gameList.values();
    }

}
