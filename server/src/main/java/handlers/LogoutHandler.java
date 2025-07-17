package handlers;

import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final LogoutService logoutService;
    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public Object handle(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            logoutService.logoutService(authToken);
            response.status(200);
            return "{}";
        }
        catch (Exception e) {
            if (e.getMessage().equals("Error: unauthorized")) {
                response.status(401);
            }
            else {
                response.status(500);
            }

            return String.format("{\"message\": \"%s\"}",e.getMessage());
        }
    }
}
