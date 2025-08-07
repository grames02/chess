package model;

import chess.ChessMove;

public class MakeMoveRequest {
    private String authToken;
    private ChessMove move;
    private int gameId;

    public MakeMoveRequest(String authToken, int gameId, ChessMove move){
        this.authToken = authToken;
        this.move = move;
        this.gameId = gameId;
    }
}
