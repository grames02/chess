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
        dao.clear_all();
    }

    // Now we'll test the functions.
    @Test
    void testCreate_GetUser_Good() throws DataAccessException {
        UserData user = new UserData("example_username", "cool_password", "even_cooler_email");
        dao.createUser(user);
        UserData fromDb = dao.getUser(user.username());
        assertNotNull(fromDb);
        assertEquals(user.username(), fromDb.username());
        assertNotEquals("unhashed_password", fromDb.password());
    }




}
