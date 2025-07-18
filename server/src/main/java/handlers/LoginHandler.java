package handlers;

import com.google.gson.Gson;
import model.LoginRequest;
import model.LoginResult;
import service.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final LoginService loginService;
    private final Gson gson = new Gson();

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public Object handle(Request request, Response response) {
        try {
            LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

            if (loginRequest == null || loginRequest.username() == null || loginRequest.password() == null) {
                response.status(400);
                return gson.toJson(new ErrorMessage("Error: bad request"));
            }
            LoginResult authData = loginService.loginService(loginRequest);

            response.status(200);
            return gson.toJson(authData);

        } catch (Exception e) {
            if ("Error: unauthorized".equals(e.getMessage())) {
                response.status(401);
                return gson.toJson(new ErrorMessage(e.getMessage()));
            } else {
                response.status(500);
                return gson.toJson(new ErrorMessage("Error: " + e.getMessage()));
            }
        }
    }
    private record ErrorMessage(String message) {}
}
