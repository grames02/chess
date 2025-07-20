package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Collection;

public class MySqlDataAccess implements DataAccess {
    public void clear_all() throws DataAccessException {

    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public void createUser(UserData user) throws DataAccessException {

        String sql = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.username());
                stmt.setString(2, user.password());
                stmt.setString(3, user.email());
                stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert user, e");
        }

    }

    public void createAuth(AuthData auth) throws DataAccessException {

    }

    public void createGame(GameData game) throws DataAccessException {

    }

    public void updateGame(GameData game) throws DataAccessException {

    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }
    public GameData getGame(int game_id) throws DataAccessException {
        return null;
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return null;
    }


}
