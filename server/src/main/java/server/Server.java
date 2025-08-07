package server;

import dataaccess.*;
import handlers.*;
import service.*;
import spark.*;
import websocket.ChessWebSocket;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", ChessWebSocket.class);

        // Variables to bring in.
        try {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();
        } catch (Exception e) {

        }
        var dataAccess = new MySqlDataAccess();

        var clearService = new ClearService(dataAccess);
        var clearHandler = new ClearHandler(clearService);

        var registerService = new RegisterService(dataAccess);
        var registerHandler = new RegisterHandler(registerService);

        var createGameService = new CreateGameService(dataAccess);
        var createGameHandler = new CreateGameHandler(createGameService);

        var joinGameService = new JoinGameService(dataAccess);
        var joinGameHandler = new JoinGameHandler(joinGameService);

        var listGamesService = new ListGamesService(dataAccess);
        var listGameHandler = new ListGamesHandler(listGamesService);

        var loginService = new LoginService(dataAccess);
        var loginHandler = new LoginHandler(loginService);

        var logoutService = new LogoutService(dataAccess);
        var logoutHandler = new LogoutHandler(logoutService);

        // Register your endpoints and handle exceptions here.

        // Clear
        Spark.delete("/db", clearHandler::handle);

        // Register
        Spark.post("/user", registerHandler::handle);

        // Login
        Spark.post("/session", loginHandler::handle);

        // Logout
        Spark.delete("/session", logoutHandler::handle);

        // List game
        Spark.get("/game", listGameHandler::handle);

        // Create game
        Spark.post("/game", createGameHandler::handle);

        // Join game
        Spark.put("/game", joinGameHandler::handle);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
