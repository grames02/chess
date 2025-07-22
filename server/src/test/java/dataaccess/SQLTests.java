package dataaccess;

import chess.ChessGame;
import model.*;
import org.eclipse.jetty.server.Authentication;
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
    // Create User
    @Test
    //Good
    void testCreate_GetUser_Good() throws DataAccessException {
        UserData user = new UserData("example_username", "cool_password", "even_cooler_email");
        dao.createUser(user);
        UserData fromDb = dao.getUser(user.username());
        assertNotNull(fromDb);
        assertEquals(user.username(), fromDb.username());
        assertNotEquals("unhashed_password", fromDb.password());
    }

    @Test
    // Bad
    void testCreate_DuplicateUser_Bad() throws DataAccessException {
        UserData user = new UserData("example_username", "cool_password", "even_cooler_email");
        dao.createUser(user);
        assertThrows(DataAccessException.class, () -> dao.createUser(user));
    }


    // Getting AuthToken
    // Good
    @Test
    void testCreate_getAuth_good() throws DataAccessException {
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
    void testCreate_getAuth_badDoesNotExist() throws DataAccessException {
        UserData res = dao.getUser("does not exist");
        assertNull(res);
    }
    // Delete AuthToken
    @Test
    void testDeleteAuth_good() throws DataAccessException{
        dao.createUser(new UserData("another_dope_user", "another_sweet_password", "howdy@gmail.com"));
        AuthData auth = new AuthData("authtoken2.0", "another_dope_user");
        dao.createAuth(auth);
        dao.deleteAuth("authtoken2.0");
        AuthData hopefullyNull = dao.getAuth("authtoken2.0");
        assertNull(hopefullyNull);
    }
    // Duplicate AuthToken
    @Test
    void test_make_duplicate_auth_token() throws DataAccessException {
        dao.createUser(new UserData("example_username", "cool_password", "even_cooler_email"));
        AuthData auth = new AuthData("some_cool_authtoken", "example_username");
        dao.createAuth(auth);
        assertThrows(DataAccessException.class, () -> dao.createAuth(auth));
    }
}
