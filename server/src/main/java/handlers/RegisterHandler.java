package handlers;

import service.RegisterService;
import spark.Request;
import spark.Response;
import model.RegisterRequest;
import model.AuthData;
import com.google.gson.Gson;


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
            response.status(400);
            return gson.toJson("Error");
        }
    }
}
