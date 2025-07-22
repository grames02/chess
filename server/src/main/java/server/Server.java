package server;

import dataaccess.*;
import handlers.*;
import service.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Variables to bring in.
        var dataAccess = new MySqlDataAccess();

        var clearService = new ClearService(dataAccess);
        var clearHandler = new ClearHandler(clearService);

        var registerService = new RegisterService(dataAccess);
        var registerHandler = new RegisterHandler(registerService);

        var CreategameService = new CreateGameService(dataAccess);
        var CreategameHandler = new CreateGameHandler(CreategameService);

        var JoingameService = new JoinGameService(dataAccess);
        var JoingameHandler = new JoinGameHandler(JoingameService);

        var ListGamesService = new ListGamesService(dataAccess);
        var ListGameHandler = new ListGamesHandler(ListGamesService);

        var LoginService = new LoginService(dataAccess);
        var LoginHandler = new LoginHandler(LoginService);

        var LogoutService = new LogoutService(dataAccess);
        var LogoutHandler = new LogoutHandler(LogoutService);

        // Register your endpoints and handle exceptions here.

        // Clear
        Spark.delete("/db", clearHandler::handle);

        // Register
        Spark.post("/user", registerHandler::handle);

        // Login
        Spark.post("/session", LoginHandler::handle);

        // Logout
        Spark.delete("/session", LogoutHandler::handle);

        // List game
        Spark.get("/game", ListGameHandler::handle);

        // Create game
        Spark.post("/game", CreategameHandler::handle);

        // Join game
        Spark.put("/game", JoingameHandler::handle);


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
