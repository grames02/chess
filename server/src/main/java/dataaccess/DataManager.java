package dataaccess;
import model.*;
import java.util.*;

public class DataManager implements DataAccess {
    private final Map<String, UserData> user_list = new HashMap<>();
    private final Map<String, UserData> auth_codes = new HashMap<>();
    private final Map<Integer, GameData> game_list = new HashMap<>();
    // Register


    // Login


    // Logout


    // Create Game


    // Get Game List


    // Join Game


    // Clear App.
    public void clear_all() throws DataAccessException {
        auth_codes.clear();
        game_list.clear();
        user_list.clear();
    }
}
