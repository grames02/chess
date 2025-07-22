package handlers;

import service.JoinGameService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import model.JoinGameRequest;

public class JoinGameHandler {
    private final JoinGameService joinGameService;
    private final Gson gson = new Gson();
    public JoinGameHandler(JoinGameService joingameService) {
        this.joinGameService = joingameService;
    }

    public Object handle(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
            if (joinGameRequest == null || joinGameRequest.gameID() == null) {
                response.status(400);
                return gson.toJson(new Error("Error: bad request"));
            }
            joinGameService.joinGameService(authToken, joinGameRequest);
            response.status(200);
            return "{}";
        }
        catch (Exception e) {
            if (e.getMessage().contains("unauthorized")) {
                response.status(401);
            } else if (e.getMessage().contains("already taken")) {
                response.status(403);
            } else if (e.getMessage().contains("invalid player color")) {
                response.status(400);
            }
            else {
                response.status(500);
            }
            return gson.toJson(new Error(e.getMessage()));
        }
    }
    private record Error(String message) {}
}
