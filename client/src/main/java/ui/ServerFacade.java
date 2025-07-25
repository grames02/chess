package ui;

import model.AuthData;
import model.LoginRequest;
import com.google.gson.Gson;
import model.RegisterRequest;

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

    public AuthData login(String username, String password) throws IOException {
        var loginReq = new LoginRequest(username, password);
        URL url = new URL(baseUrl + "/session");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(gson.toJson(loginReq).getBytes());
        }
        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ?
                connection.getInputStream() : connection.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                return gson.fromJson(reader, AuthData.class);
            }
            else {
                throw new IOException("Login Failed");
            }
        }
    }

    public AuthData register(String username, String password, String email) throws IOException {
        var registerReq = new RegisterRequest(username, password, email);
        URL url = new URL(baseUrl + "/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(gson.toJson(registerReq).getBytes());
        }
        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ?
                connection.getInputStream() : connection.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                return gson.fromJson(reader, AuthData.class);
            }
            else {
                throw new IOException("Registration Failed");
            }
        }
    }

    public AuthData createGame(String gameName) {

    }

    public AuthData listGames() {
    }

    public AuthData joinGame() {
    }
}
