package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
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
    private static final Map<Integer, Set<Session>> GAME_SESSIONS = new ConcurrentHashMap<>();
    private static final Map<Session, String> SESSION_USERNAMES = new ConcurrentHashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s%n", message);
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
                GAME_SESSIONS.computeIfAbsent(gameId, k -> new CopyOnWriteArraySet<>()).add(session);
                SESSION_USERNAMES.put(session, auth.username());
                ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, fullGame);
                session.getRemote().sendString(gson.toJson(loadGame));
                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        auth.username() + " has joined the game.");
                Set<Session> sessions = GAME_SESSIONS.get(gameId);
                if (sessions != null) {
                    for (Session s : sessions) {
                        if (!s.equals(session) && s.isOpen()) {
                            s.getRemote().sendString(gson.toJson(notification));
                        }
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
                dataAccess.updateGame(updatedGame);
                Set<Session> sessions = GAME_SESSIONS.get(gameId);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        GAME_SESSIONS.remove(gameId);
                    }
                }
                SESSION_USERNAMES.remove(session);
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
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. " + e.getMessage());
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                dataAccess.updateGame(fullGame);
                GameData updatedGame = dataAccess.getGame(gameId);
                if (updatedGame == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Failed to fetch updated game.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, updatedGame);
                Set<Session> sessionsForGame = GAME_SESSIONS.get(gameId);
                if (sessionsForGame != null) {
                    for (Session s : sessionsForGame) {
                        if (s.isOpen()) {
                            s.getRemote().sendString(gson.toJson(loadGame));
                        }
                    }
                }
                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        auth.username() + " made a move.");
                if (sessionsForGame != null) {
                    for (Session s : sessionsForGame) {
                        if (!s.equals(session) && s.isOpen()) {
                            s.getRemote().sendString(gson.toJson(notification));
                        }
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

                game.resign(ChessGame.TeamColor.valueOf(playerTeam));
                dataAccess.updateGame(fullGame);
                GameData updatedGame = dataAccess.getGame(gameId);
                if (updatedGame == null) {
                    ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "ERROR. Failed to fetch updated game.");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, updatedGame);
                Set<Session> sessionsForGame = GAME_SESSIONS.get(gameId);
                if (sessionsForGame != null) {
                    for (Session s : sessionsForGame) {
                        if (s.isOpen()) {
                            s.getRemote().sendString(gson.toJson(loadGame));
                        }
                    }
                }

                String notificationText = auth.username() + " (" + playerTeam + ") has resigned. Game over.";
                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationText);

                if (sessionsForGame != null) {
                    for (Session s : sessionsForGame) {
                        if (s.isOpen()) {
                            s.getRemote().sendString(gson.toJson(notification));
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error Processing WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        SESSION_USERNAMES.remove(session);
        GAME_SESSIONS.values().forEach(sessions -> sessions.remove(session));
    }
}
