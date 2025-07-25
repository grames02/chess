package ui;

import model.AuthData;
import model.LoginRequest;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

public class ServerFacade {
    private final String baseUrl;
    private final Gson gson = new Gson();
    public ServerFacade(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public AuthData login(String username, String password) throws IOException{
        var loginReq = new LoginRequest(username, password);
        URL url = new URL(baseUrl + "/session");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // RESUME FROM HERE.
}
