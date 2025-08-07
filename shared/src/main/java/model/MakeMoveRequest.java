package model;

import chess.ChessMove;

public class MakeMoveRequest {
    private String authToken;
    private ChessMove move;

    public MakeMoveRequest(String authToken, ChessMove move){
        this.authToken = authToken;
        this.move = move;
    }
}
