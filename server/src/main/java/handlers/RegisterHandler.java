package handlers;

import service.RegisterService;
import spark.Request;
import spark.Response;
import model.RegisterRequest;
import model.AuthData;

public class RegisterHandler {
    private final RegisterService registerService;
    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public Object handle(Request request, Response response) {
        return null;
    }
}
