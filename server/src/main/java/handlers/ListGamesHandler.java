package handlers;

import service.ListGamesService;
import spark.Request;
import spark.Response;
import model.GameData;
import com.google.gson.Gson;
import java.util.Collection;

public class ListGamesHandler {
    private final ListGamesService listGamesService;
    public ListGamesHandler(ListGamesService listgameService) {
        this.listGamesService = listgameService;
    }

    public Object handle(Request request, Response response) {
        try {
            String authToken = request.headers("authorization");
            Collection<GameData> games = listGamesService.listGameService(authToken);
            response.status(200);
            return new Gson().toJson(new GameListResponse(games));
        } catch (Exception e) {
            if ("Error: unauthorized".equals(e.getMessage())) {
                response.status(401);
            } else {
                response.status(500);
            }
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }
    }

    private record GameListResponse(Collection<GameData> games) {}
    private record ErrorResponse(String message) {}
}
