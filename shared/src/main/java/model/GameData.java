package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData withWhiteUsername(String newWhite) {
        return new GameData(gameID, newWhite, blackUsername, gameName, game);
    }

    public GameData withBlackUsername(String newBlack) {
        return new GameData(gameID, whiteUsername, newBlack, gameName, game);
    }

}
