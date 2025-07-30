package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import model.AuthData;
import model.ListGamesResponse;
import server.Server;
import ui.ServerFacade;

import java.io.IOException;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        String baseUrl = "http://localhost:" + port;
        System.out.println(baseUrl);
        facade = new ServerFacade(baseUrl);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clearDatabase();
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void testRegisterDuplicateUsername() throws IOException {
        facade.register("user1", "pass1", "email1");
        Exception exception = assertThrows(Exception.class, () -> {
            facade.register("user1", "pass2", "email2");
        });
        System.out.println("Exception message: " + exception.getMessage());
        String msg = exception.getMessage().toLowerCase();
        assertTrue(msg.contains("failed") || msg.contains("duplicate") || msg.contains("exists"));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        facade.register("player2", "password", "p2@email.com");
        AuthData authData = facade.login("player2", "password");
        assertNotNull(authData);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void testLoginFailWrongPassword() throws Exception {
        facade.register("player3", "password", "p3@email.com");
        Exception exception = assertThrows(Exception.class, () -> {
            facade.login("player3", "wrongpassword");
        });
        System.out.println("Exception message: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));
    }

    @Test
    public void testCreateGameSuccess() throws Exception {
        AuthData authData = facade.register("player4", "password", "p4@email.com");
        facade.createGame("Test Game", authData.authToken());
        ListGamesResponse gamesResponse = facade.listGames(authData.authToken());
        assertFalse(gamesResponse.getGames().isEmpty());
        assertTrue(gamesResponse.getGames().stream().anyMatch(g -> g.gameName().equals("Test Game")));
    }

    @Test
    public void testCreateGameInvalidToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.createGame("Invalid Token Game", "badtoken");
        });
        System.out.println("Exception message: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("failed"));
    }

    @Test
    public void testListGamesSuccess() throws Exception {
        AuthData authData = facade.register("player5", "password", "p5@email.com");
        facade.createGame("List Test Game", authData.authToken());
        ListGamesResponse response = facade.listGames(authData.authToken());
        assertNotNull(response);
        assertFalse(response.getGames().isEmpty());
    }

    @Test
    public void testJoinGameSuccess() throws Exception {
        AuthData authData = facade.register("joinUser", "password", "join@example.com");
        facade.createGame("Test Join Game", authData.authToken());
        ListGamesResponse listResponse = facade.listGames(authData.authToken());
        assertFalse(listResponse.getGames().isEmpty(), "Game list should not be empty");
        int gameNumber = 1;
        ChessGame game = facade.joinGame(authData.authToken(), "white", gameNumber);
        assertNotNull(game);
    }

    @Test
    public void testJoinGameInvalidGameNumber() throws Exception {
        AuthData authData = facade.register("player7", "password", "p7@email.com");
        int invalidGameNumber = 999;
        Exception exception = assertThrows(Exception.class, () -> {
            facade.joinGame(authData.authToken(), "White", invalidGameNumber);
        });
        System.out.println("Exception message: " + exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("invalid game number") ||
                exception.getMessage().toLowerCase().contains("failed"));
    }


    @Test
    public void testLogoutSuccess() throws Exception {
        AuthData authData = facade.register("player8", "password", "p8@email.com");
        facade.logout(authData.authToken());
    }

    @Test
    public void testLogoutInvalidToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.logout("invalidtoken");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("unauthorized") || exception.getMessage().toLowerCase().contains("token"));
    }

    @Test
    public void testListGamesInvalidToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            facade.listGames("invalidtoken");
        });
        String msg = exception.getMessage().toLowerCase();
        assertTrue(msg.contains("failed") || msg.contains("unauthorized") || msg.contains("token"));
    }
}
