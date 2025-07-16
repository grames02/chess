package dataaccess;
import model.*;

import java.util.*;

public class DataManager implements DataAccess {
    private final Map<String, UserData> user_list = new HashMap<>();
    private final Map<String, UserData> auth_codes = new HashMap<>();
    private final Map<Integer, GameData> game_list = new HashMap<>();
    // Register
    public void register() throws DataAccessException {

    }

    // Login
    public void login() throws DataAccessException {

    }

    // Logout
    public void logout() throws DataAccessException {

    }

    // Create Game
    public void create_game() throws DataAccessException {

    }

    // Get Game List
    public void get_game_list() throws DataAccessException {

    }


    // Join Game
    public void join_game() throws DataAccessException {

    }


    // Clear App.
    public void clear_all() throws DataAccessException {
        auth_codes.clear();
        game_list.clear();
        user_list.clear();
    }
}
