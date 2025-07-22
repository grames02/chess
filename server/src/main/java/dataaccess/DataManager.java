package dataaccess;
import model.*;

import javax.xml.crypto.Data;
import java.util.*;

public class DataManager implements DataAccess {
    private final Map<String, UserData> user_list = new HashMap<>();
    private final Map<String, AuthData> auth_codes = new HashMap<>();
    private final Map<Integer, GameData> game_list = new HashMap<>();

    // Clear App.
    public void clearAll() throws DataAccessException {
        auth_codes.clear();
        game_list.clear();
        user_list.clear();
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auth_codes.remove(authToken);
    }

    public void createUser(UserData user) throws DataAccessException {
        if (user_list.containsKey(user.username())) {
            throw new DataAccessException("User already exists.");
        }
        user_list.put(user.username(), user);
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        auth_codes.put(auth.authToken(), auth);
    }

    public void createGame(GameData game) throws DataAccessException {
        game_list.put(game.gameID(), game);
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (!game_list.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found.");
        }
        game_list.put(game.gameID(), game);
    }

    public UserData getUser(String username) throws DataAccessException {
        return user_list.get(username);
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auth_codes.get(authToken);
    }
    public GameData getGame(int game_id) throws DataAccessException {
        return game_list.get(game_id);
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return game_list.values();
    }

}
