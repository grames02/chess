package handlers;

import service.CreateGameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService creategameService) {
        this.createGameService = creategameService;
    }

    public Object handle(Request request, Response response) {
        try {
            createGameService.createGameService();
            response.status(200);
            return "{ \"gameID\": 1234 }";
        } catch (Exception e) {
            response.status(500);
            return "{\"message\": \"Error: (description of error)\"}";

        }
    }
}
