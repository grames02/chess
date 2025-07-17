package handlers;

import service.RegisterService;
import spark.Request;
import spark.Response;
import model.RegisterRequest;
import model.AuthData;
import com.google.gson.Gson;

import java.util.Map;


public class RegisterHandler {
    private final RegisterService registerService;
    private final Gson gson = new Gson();
    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public Object handle(Request request, Response response) {
        try {
            RegisterRequest registerRequest = gson.fromJson(request.body(),RegisterRequest.class);
            AuthData result = registerService.register(registerRequest);
            response.status(200);
            return gson.toJson(result);
        }
        catch (Exception e) {
            if (e.getMessage().equals("Error: bad request")) {
                response.status(400);
            }
            else if (e.getMessage().equals("Error: already taken")) {
                response.status(403);
            }
            else {
                response.status(500);
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}
