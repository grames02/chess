package websocket;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;

@WebSocket
public class ChessWebSocket {
    private Gson gson = new Gson();
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s", message);
        session.getRemote().sendString(message);

        try {
            UserGameCommand gameCommand = gson.fromJson(message, UserGameCommand.class);
            if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.CONNECT)) {

            }
            else if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.LEAVE)) {

            }
            else if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
                // I could implement the request that the user made in the UI. Passing that variable into here.
                // That variable will be requestedMove.

            }
            else if (gameCommand.getCommandType().equals(UserGameCommand.CommandType.RESIGN)) {

            }
            else {
                // That's no good.

            }
        } catch (Exception e) {
            // Print error here.

        }

    }

    public boolean makeMove(ChessMove requestedMove) {
        return true;
    }
}