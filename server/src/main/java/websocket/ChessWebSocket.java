package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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

                String username = auth.username();
                GameData updatedGame = fullGame;

                if (username.equals(fullGame.whiteUsername())) {
                    updatedGame = fullGame.withWhiteUsername(null);
                } else if (username.equals(fullGame.blackUsername())) {
                    updatedGame = fullGame.withBlackUsername(null);
                }

                // Update game in DB
                dataAccess.updateGame(updatedGame);

                // Update in-memory map if you keep a cache (optional)
                // For example, replace fullGame with updatedGame in your in-memory structure

                // Remove session from gameSessions and sessionUsernames maps
                Set<Session> sessions = gameSessions.get(gameId);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        gameSessions.remove(gameId);
                    }
                }
                sessionUsernames.remove(session);

                // Notify remaining sessions except leaving one
                if (sessions != null) {
                    ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            username + " has left the game.");
                    for (Session s : sessions) {
                        if (s.isOpen()) {
                            s.getRemote().sendString(gson.toJson(notification));
                        }
                    }
                }
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

                String currentTurn = game.getTeamTurn().name();

                String playerTeam;
                if (auth.username().equals(fullGame.whiteUsername())) {
                    playerTeam = "WHITE";
                } else if (auth.username().equals(fullGame.blackUsername())) {
                    playerTeam = "BLACK";
                } else {
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

                try {
                    game.makeMove(move);
                } catch (InvalidMoveException e) {
                    // *** Send an ERROR message back here ***
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. " + e.getMessage());
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

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

                // Determine the player's team color by username
                String playerTeam;
                if (auth.username().equals(fullGame.whiteUsername())) {
                    playerTeam = "WHITE";
                } else if (auth.username().equals(fullGame.blackUsername())) {
                    playerTeam = "BLACK";
                } else {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Player not in game.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                if (game.isOver()) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                            "ERROR. Game is already over; cannot resign again.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }

                // Mark the game as resigned and over (you'll need to add methods or flags for this in your ChessGame/GameData classes)
                game.resign(ChessGame.TeamColor.valueOf(playerTeam)); // example method — implement in your ChessGame model
                game.getResignedPlayer(true);              // example method — implement in your ChessGame model

                // Update the data in your database
                dataAccess.updateGame(fullGame);

                // Notify all sessions connected to this game about the resignation
                String notificationText = auth.username() + " (" + playerTeam + ") has resigned. Game over.";

                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationText);

                for (Session s : gameSessions.get(gameId)) {
                    if (s.isOpen()) {
                        s.getRemote().sendString(gson.toJson(notification));
                    }
                }
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

}