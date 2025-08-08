package ui;

import chess.ChessGame;
import chess.ChessMove;
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
import chess.*;

public class ServerFacade {
    private ChessWebSocketCLIENT webSocket;
    private final String baseUrl;
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ClientUI ui;

    public ServerFacade(String baseUrl, ClientUI ui) {
        this.baseUrl = baseUrl;
        this.ui = ui;
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
        if (gameNumber <= 0) {
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

        ChessGame game;
        try (OutputStream os = connection.getOutputStream()) {
            os.write(gson.toJson(joinGameReq).getBytes());
        }

        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ? connection.getInputStream() : connection.getErrorStream();
        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                game = gson.fromJson(reader, ChessGame.class);
            } else {
                throw new IOException("Join Game Failed");
            }
        }

        try {
            if (webSocket == null || !webSocket.isOpen()) {
                webSocket = new ChessWebSocketCLIENT("ws://localhost:8080/ws", ui);
            }
            ConnectRequest connectRequest = new ConnectRequest(authToken, gameID);
            String msg = gson.toJson(connectRequest);
            webSocket.send(msg);

        } catch (Exception e) {
            System.out.print("Invalid Connect request. ERROR.");
        }
        return game;
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
        int gameID = listResponse.getGames().get(gameNumber - 1).gameID();

        URL url = new URL(baseUrl + "/game/" + gameID);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", authToken);
        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ?
                connection.getInputStream() : connection.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                GameData gameData = gson.fromJson(reader, GameData.class);
                ChessGame game = convertGameDataToChessGame(gameData);
                return convertBoardToCharArray(game.getBoard());
            } else {
                throw new IOException("Observe Game Failed");
            }
        }
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

    public char[][] makeMove(String authToken, int gameId, ChessPosition start, ChessPosition end) throws Exception {
        MakeMoveRequest moveRequest = new MakeMoveRequest(authToken, gameId, new ChessMove(start, end, null));
        URL url = new URL(baseUrl + "/game/" + gameId + "/move");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", authToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(gson.toJson(moveRequest).getBytes());
        }

        int respCode = connection.getResponseCode();
        InputStream responseStream = (respCode == 200) ?
                connection.getInputStream() : connection.getErrorStream();

        try (InputStreamReader reader = new InputStreamReader(responseStream)) {
            if (respCode == 200) {
                // Assuming server returns updated board state as char[][]
                return gson.fromJson(reader, char[][].class);
            } else {
                throw new IOException("Make Move Failed");
            }
        }
    }

    public ChessGame convertGameDataToChessGame(GameData gameData) {
        // Minimal example conversion. This must be adjusted based on actual GameData and ChessGame classes.
        ChessGame game = new ChessGame();
        // Assume gameData contains board info, etc. This needs proper implementation.
        // For now, just return empty game
        return game;
    }

    public ChessWebSocketCLIENT getWebSocket() {
        return webSocket;
    }

    public Gson getGson() {
        return gson;
    }

    public static char[][] convertBoardToCharArray(ChessBoard board) {
        char[][] boardChars = new char[8][8];
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    boardChars[row - 1][col - 1] = ' ';  // empty square
                } else {
                    boardChars[row - 1][col - 1] = pieceToChar(piece);
                }
            }
        }
        return boardChars;
    }

    private static char pieceToChar(ChessPiece piece) {
        char c;
        switch (piece.getPieceType()) {
            case PAWN: c = 'p'; break;
            case ROOK: c = 'r'; break;
            case KNIGHT: c = 'n'; break;
            case BISHOP: c = 'b'; break;
            case QUEEN: c = 'q'; break;
            case KING: c = 'k'; break;
            default: c = ' '; break;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            c = Character.toUpperCase(c);
        }
        return c;
    }
}
