package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import model.GameData;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class ChessWebSocketCLIENT {

    private Session session;
    private final URI serverUri;
    private final ClientUI ui;

    public ChessWebSocketCLIENT(String serverUrl, ClientUI ui) throws Exception {
        this.serverUri = new URI(serverUrl);
        this.ui = ui;
        connect();
    }

    private void connect() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, serverUri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket connection opened.");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received from server: " + message);
        Gson gson = new Gson();

        try {
            // First, just get the type
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("serverMessageType").getAsString();

            switch (type) {
                case "LOAD_GAME" -> {
                    GameData gameData = gson.fromJson(json.get("game"), GameData.class);
                    if (gameData != null) {
                        ui.updateLatestGameData(gameData);
                        char[][] boardChars = ui.convertBoardToCharArray(gameData.game().getBoard());
                        ChessBoardDrawer.drawBoard(boardChars, ui.fromWhitePerspective);
                    } else {
                        System.err.println("LOAD_GAME message missing game data.");
                    }
                }
                case "BOARD_UPDATE" -> {
                    GameData gameData = gson.fromJson(json.get("game"), GameData.class);
                    if (gameData != null) {
                        ui.updateLatestGameData(gameData);
                        char[][] boardChars = ui.convertBoardToCharArray(gameData.game().getBoard());
                        ChessBoardDrawer.drawBoard(boardChars, ui.fromWhitePerspective);
                    } else {
                        System.err.println("BOARD_UPDATE message missing game data.");
                    }
                }
                case "ERROR" -> {
                    System.err.println("Server error: " + json.get("errorMessage").getAsString());
                }
                case "NOTIFICATION" -> {
                    System.out.println("Server notification: " + json.get("message").getAsString());
                }
                default -> {
                    System.err.println("Unknown server message type: " + type);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse server message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void send(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            } else {
                System.err.println("WebSocket is not connected.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public boolean isOpen() {
        return session != null && session.isOpen();
    }

    public void close() {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing WebSocket: " + e.getMessage());
        }
    }

    public void makeLeave(String authToken, int gameID) throws Exception {
        Gson gson = new Gson();
        JsonObject message = new JsonObject();
        message.addProperty("commandType", "LEAVE");
        message.addProperty("authToken", authToken);
        message.addProperty("gameID", gameID);
        send(gson.toJson(message));

    }

    public void makeResign(String authToken, int gameId) throws Exception {
        Gson gson = new Gson();
        JsonObject message = new JsonObject();
        message.addProperty("commandType", "RESIGN");
        message.addProperty("authToken", authToken);
        message.addProperty("gameID", gameId);

        send(gson.toJson(message));
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws Exception {
        Gson gson = new Gson();

        JsonObject moveJson = new JsonObject();
        JsonObject start = new JsonObject();
        JsonObject end = new JsonObject();

        start.addProperty("row", move.getStartPosition().getRow());
        start.addProperty("col", move.getStartPosition().getColumn());
        end.addProperty("row", move.getEndPosition().getRow());
        end.addProperty("col", move.getEndPosition().getColumn());

        moveJson.add("start", start);
        moveJson.add("end", end);
        moveJson.add("promotionPiece", move.getPromotionPiece() == null ? JsonNull.INSTANCE : new JsonPrimitive(move.getPromotionPiece().name()));

        JsonObject message = new JsonObject();
        message.addProperty("commandType", "MAKE_MOVE");
        message.addProperty("authToken", authToken);
        message.addProperty("gameID", gameId);
        message.add("move", moveJson);

        send(gson.toJson(message));
    }

    public void sendConnect(String authToken, int gameId) {
        Gson gson = new Gson();
        JsonObject message = new JsonObject();
        message.addProperty("commandType", "CONNECT");
        message.addProperty("authToken", authToken);
        message.addProperty("gameID", gameId);
        send(gson.toJson(message));
    }
}
