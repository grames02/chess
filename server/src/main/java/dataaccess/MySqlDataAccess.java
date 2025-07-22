package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySqlDataAccess implements DataAccess {




    @Override
    public void clearAll() throws DataAccessException {
        String[] tables = {"authdata", "userdata", "gamedata"};
        try (var conn = DatabaseManager.getConnection()) {
            for (String table : tables) {
                try (var stmt = conn.prepareStatement("DELETE FROM " + table)) {
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to clear all tables: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM authdata WHERE authtoken = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to delete auth token: " + e.getMessage(), e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to insert user: " + e.getMessage(), e);
        }
    }
    //Pls work.
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO authdata (authtoken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to insert auth token: " + e.getMessage(), e);
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO gamedata (gameid, whiteusername, blackusername, gamename, game) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.gameID());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, game.gameName());
            stmt.setString(5, new Gson().toJson(game.game()));
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to insert game: " + e.getMessage(), e);
        }
    }


    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE gamedata SET gamename = ?, whiteusername = ?, blackusername = ?, game = ? WHERE gameid = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, new Gson().toJson(game.game()));
            stmt.setInt(5, game.gameID());
            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new DataAccessException("Error No game found with ID: " + game.gameID());
            }
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to update game: " + e.getMessage(), e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM userdata WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to get user: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authtoken, username FROM authdata WHERE authtoken = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authtoken"),
                            rs.getString("username")
                    );
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to get auth token: " + e.getMessage(), e);
        }
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        String sql = "SELECT gameid, whiteusername, blackusername, gamename, game FROM gamedata WHERE gameid = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                    return new GameData(
                            rs.getInt("gameid"),
                            rs.getString("whiteusername"),
                            rs.getString("blackusername"),
                            rs.getString("gamename"),
                            game
                    );
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error Failed to get game: " + e.getMessage(), e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT gameid, whiteusername, blackusername, gamename, game FROM gamedata";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("gameid");
                String name = rs.getString("gamename");
                String white = rs.getString("whiteusername");
                String black = rs.getString("blackusername");
                ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                games.add(new GameData(id, white, black, name, game));
            }


        } catch (Exception e) {
            throw new DataAccessException("Error Failed to list games: " + e.getMessage(), e);
        }
        return games;
    }
}
