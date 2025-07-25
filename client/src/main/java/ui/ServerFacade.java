package ui;

import model.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
            } else {
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
            } else {
                throw new IOException("Registration Failed");
            }
        }
    }

    public CreateGameResponse createGame(String gameName, String authToken) throws IOException {
        var createGameReq = new CreateGameRequest(gameName);
        URL url = new URL(baseUrl + "/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(gson.toJson(createGameReq).getBytes());
        }
        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ?
                connection.getInputStream() : connection.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                return gson.fromJson(reader, CreateGameResponse.class);
            } else {
                throw new IOException("Game Creation Failed");
            }
        }
    }

    public ListGamesResponse listGames(String authToken) throws IOException {
        URL url = new URL(baseUrl + "/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);
        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ?
                connection.getInputStream() : connection.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                return gson.fromJson(reader, ListGamesResponse.class);
            } else {
                throw new IOException("List Games Failed");
            }
        }
    }

    public void joinGame(String authToken, String playerColor, int gameId) throws IOException {
        JoinGameRequest joinGameReq = new JoinGameRequest(playerColor, gameId);
        URL url = new URL(baseUrl + "/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", authToken);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(gson.toJson(joinGameReq).getBytes());
        }
        int respCode = connection.getResponseCode();
        if (respCode != 200) {
            throw new IOException("Join Game Failed");
        }
    }
}
