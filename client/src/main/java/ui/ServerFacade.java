package ui;

import chess.ChessGame;
import chess.ChessPosition;
import model.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.util.List;

public class ServerFacade {
    private final String baseUrl;
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

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

    public ChessGame joinGame(String authToken, String playerColor, int gameNumber) throws IOException {
        ListGamesResponse listResponse = listGames(authToken);
        if (gameNumber <= 0 || gameNumber > listResponse.getGames().size()) {
            throw new IOException("Invalid game number");
        }
        int gameID = listResponse.getGames().get(gameNumber - 1).gameID();

        JoinGameRequest joinGameReq = new JoinGameRequest(playerColor, gameID);
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
        InputStream responseStream = (respCode == 200) ? connection.getInputStream() : connection.getErrorStream();
        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                return gson.fromJson(reader, ChessGame.class);
            } else {
                throw new IOException("Join Game Failed");
            }
        }
    }

    public void logout(String authToken) throws IOException {
        URL url = new URL(baseUrl + "/session");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", authToken);
        connection.connect();
        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ?
                connection.getInputStream() : connection.getErrorStream();
        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode != 200) {
                var errorResponse = new BufferedReader(reader).readLine();
                throw new IOException("Logout Failed: " + errorResponse);
            }
        }
    }

    public char[][] observeGame(String authToken, int gameNumber) throws IOException {
        ListGamesResponse listResponse = listGames(authToken);
        if (gameNumber <= 0 || gameNumber > listResponse.getGames().size()) {
            throw new IOException("Invalid game number");
        }

        listResponse.getGames().get(gameNumber - 1);
        return new char[][]{
                {'r','n','b','q','k','b','n','r'},
                {'p','p','p','p','p','p','p','p'},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {' ',' ',' ',' ',' ',' ',' ',' '},
                {'P','P','P','P','P','P','P','P'},
                {'R','N','B','Q','K','B','N','R'}
        };
    }
    public void clearDatabase() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/db"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to clear database: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    public char[][] makeMove(String s, ChessPosition start, ChessPosition end) {
        return null;
    }
}
