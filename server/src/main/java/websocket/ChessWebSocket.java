package websocket;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;
import java.util.Map;

@WebSocket
public class ChessWebSocket {
    private Gson gson = new Gson();
    private final DataAccess dataAccess = new MySqlDataAccess();

    // Map of gameID to sessions connected to that game
    private static final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();

    // Map session to username (to identify who is who)
    private static final Map<Session, String> sessionUsernames = new ConcurrentHashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s", message);
        try {
            UserGameCommand gameCommand = gson.fromJson(message, UserGameCommand.class);

            if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.CONNECT)) {
                int gameId = gameCommand.getGameID();
                String authToken = gameCommand.getAuthToken();
                AuthData auth = dataAccess.getAuth(authToken);

                if (auth == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Invalid authToken.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                GameData fullGame = dataAccess.getGame(gameId);
                if (fullGame == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Invalid game.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                // Add session to the gameSessions map
                gameSessions.computeIfAbsent(gameId, k -> new CopyOnWriteArraySet<>()).add(session);

                // Save username associated with session for notifications
                sessionUsernames.put(session, auth.username());

                ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, fullGame);

                session.getRemote().sendString(gson.toJson(loadGame));

                // Notify other sessions in the same game that this player joined
                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        auth.username() + " has joined the game.");

                for (Session s : gameSessions.get(gameId)) {
                    if (!s.equals(session) && s.isOpen()) {
                        s.getRemote().sendString(gson.toJson(notification));
                    }
                }

            }
            else if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.LEAVE)) {

            }
            else if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
                int gameId = gameCommand.getGameID();
                String authToken = gameCommand.getAuthToken();
                AuthData auth = dataAccess.getAuth(authToken);

                if (auth == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Invalid authToken.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                GameData fullGame = dataAccess.getGame(gameId);
                if (fullGame == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Invalid game.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                ChessGame game = fullGame.game();

                // Check if it is the player's turn
                String currentTurn = game.getTeamTurn().name(); // Assuming currentTurn() returns TeamColor enum (WHITE or BLACK)

                // Determine player's team color by username
                String playerTeam;
                if (auth.username().equals(fullGame.whiteUsername())) {
                    playerTeam = "WHITE";
                } else if (auth.username().equals(fullGame.blackUsername())) {
                    playerTeam = "BLACK";
                } else {
                    // Player not part of this game
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Player not in game.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                if (!currentTurn.equals(playerTeam)) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Not your turn.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                ChessMove move = gameCommand.getMove();

                var validMoves = game.validMoves(move.getStartPosition());
                if (!validMoves.contains(move)) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Invalid move.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                game.makeMove(move);
                dataAccess.updateGame(fullGame);

                ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, fullGame);

                for (Session s : gameSessions.get(gameId)) {
                    if (s.isOpen()) {
                        s.getRemote().sendString(gson.toJson(loadGame));
                    }
                }
                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        auth.username() + " made a move.");
                for (Session s : gameSessions.get(gameId)) {
                    if (!s.equals(session) && s.isOpen()) {
                        s.getRemote().sendString(gson.toJson(notification));
                    }
                }
            }


            else if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.RESIGN)) {

            }
            else {
                // That's no good.

            }
        } catch (Exception e) {
            // Print error here.
            System.out.println("Error Processing WebSocket" + e.getMessage());
        }

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessionUsernames.remove(session);
        gameSessions.values().forEach(sessions -> sessions.remove(session));
    }

    public boolean makeMove(ChessMove requestedMove) {
        return true;
    }
}