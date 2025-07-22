package handlers;

import service.CreateGameService;
import spark.Request;
import spark.Response;
import model.GameData;
import model.CreateGameRequest;
import com.google.gson.Gson;

import java.util.Map;

public class CreateGameHandler {
    private final CreateGameService createGameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(CreateGameService creategameService) {
        this.createGameService = creategameService;
    }

    public Object handle(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            CreateGameRequest gamerequest = gson.fromJson(request.body(), CreateGameRequest.class);
            GameData gameID = createGameService.createGame(gamerequest, authToken);
            response.status(200);
            return gson.toJson(gameID);
        } catch (Exception e) {
            if (e.getMessage().equals("Error: bad request")) {
                response.status(400);
            }
            else if (e.getMessage().equals("Error: unauthorized")) {
                response.status(401);
            }
            else {
                response.status(500);
            }
            return new Gson().toJson(Map.of("message",e.getMessage()));

        }
    }
}
