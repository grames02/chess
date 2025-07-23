package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {
    private static MySqlDataAccess dao;

    // Setup to ensure the database is clear before each test.
    @BeforeAll
    static void init() {
        dao = new MySqlDataAccess();
    }
    @BeforeEach
    void setup() throws DataAccessException {
        dao.clearAll();
    }

    // Now we'll test the functions.
    // Create User
    @Test
    //Good
    void testCreateGetUserGood() throws DataAccessException {
        UserData user = new UserData("example_username", "cool_password", "even_cooler_email");
        dao.createUser(user);
        UserData fromDb = dao.getUser(user.username());
        assertNotNull(fromDb);
        assertEquals(user.username(), fromDb.username());
        assertNotEquals("unhashed_password", fromDb.password());
    }

    @Test
    // Bad
    void testCreateDuplicateUserBad() throws DataAccessException {
        UserData user = new UserData("example_username", "cool_password", "even_cooler_email");
        dao.createUser(user);
        assertThrows(DataAccessException.class, () -> dao.createUser(user));
    }


    // Getting AuthToken
    // Good
    @Test
    void testCreateGetAuthGood() throws DataAccessException {
        dao.createUser(new UserData("example_username", "cool_password", "even_cooler_email"));
        AuthData auth = new AuthData("some_cool_authtoken", "example_username");
        dao.createAuth(auth);
        // Now we compare it
        AuthData fromDb = dao.getAuth("some_cool_authtoken");
        assertNotNull(fromDb);
        assertEquals("some_cool_authtoken", fromDb.authToken());
        assertEquals("example_username", fromDb.username());
    }

    @Test
    // Bad
    void testCreateGetAuthBadDoesNotExist() throws DataAccessException {
        UserData res = dao.getUser("does not exist");
        assertNull(res);
    }
    // Delete AuthToken
    @Test
    void testDeleteAuthGood() throws DataAccessException{
        dao.createUser(new UserData("another_dope_user", "another_sweet_password", "howdy@gmail.com"));
        AuthData auth = new AuthData("authtoken2.0", "another_dope_user");
        dao.createAuth(auth);
        dao.deleteAuth("authtoken2.0");
        AuthData hopefullyNull = dao.getAuth("authtoken2.0");
        assertNull(hopefullyNull);
    }
    // Duplicate AuthToken
    @Test
    void testMakeDuplicateAuthToken() throws DataAccessException {
        dao.createUser(new UserData("example_username", "cool_password", "even_cooler_email"));
        AuthData auth = new AuthData("some_cool_authtoken", "example_username");
        dao.createAuth(auth);
        assertThrows(DataAccessException.class, () -> dao.createAuth(auth));
    }

    //Create & Get game
    @Test
    void createGetGameTestGood() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "white", "black", "chess_game", game);
        dao.createGame(gameData);
        GameData fromDb = dao.getGame(1);
        assertNotNull(fromDb);
        assertEquals("chess_game", fromDb.gameName());
        assertEquals("white", fromDb.whiteUsername());
        assertEquals("black", fromDb.blackUsername());
    }

    @Test
    void listGamesTestPositive() throws DataAccessException {
        dao.createGame(new GameData(1, "white", "black", "Game 1", new ChessGame()));
        dao.createGame(new GameData(2, "white", "black", "Game 2", new ChessGame()));
        Collection<GameData> games = dao.listGames();
        assertEquals(2, games.size());
        System.out.printf(games.toString());

    }

    @Test
    void updateGamePositive() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData og = new GameData(2, "white", "black", "og", game);
        dao.createGame(og);
        GameData updated = new GameData(2, "white", "black", "new name", game);
        dao.updateGame(updated);
        GameData fromDb = dao.getGame(2);
        assertEquals("new name", fromDb.gameName());
    }

    @Test
    void clearTest() throws DataAccessException {
        dao.createUser(new UserData("yo", "sup", "dawg"));
        dao.createAuth(new AuthData("something", "yo"));
        dao.createGame(new GameData(3, "w", "b", "battle", new ChessGame()));
        dao.clearAll();

        assertNull(dao.getUser("yo"));
        assertNull(dao.getAuth("something"));
        assertNull(dao.getGame(3));
    }


    @Test
    void getBadAuthToken() throws DataAccessException {
        AuthData result = dao.getAuth("scrambled eggs");
        assertNull(result);
    }

    @Test
    void deleteImaginaryAuthToken() throws DataAccessException {
        dao.deleteAuth("this is imaginary");
        assertNull(dao.getAuth("this is imaginary"));
    }

    @Test
    void duplicateGame() throws DataAccessException {
        GameData game = new GameData(20, "w", "b", "Game_time", new ChessGame());
        dao.createGame(game);
        assertThrows(DataAccessException.class, () -> dao.createGame(game));
    }

    @Test
    void imaginaryGame() throws DataAccessException {
        GameData result = dao.getGame(99999999);
        assertNull(result);
    }

    @Test
    void updateImaginaryGame() {
        GameData game = new GameData(200, "w", "b", "not real", new ChessGame());
        assertThrows(DataAccessException.class, () -> dao.updateGame(game));
    }
}
