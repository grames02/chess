package handlers;

import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private final ClearService service;

    public ClearHandler(ClearService service) {
        this.service = service;
    }
    public Object handle(Request req, Response res) {
        try {
            service.clearApplication();
            res.status(200);
            return "{}";
        }
        catch (Exception e) {
            res.status(500);
            return "{\"message\": \"Error: (description of error)\"}";
        }
    }
}
