import chess.*;
import ui.ClientUI;
import ui.ServerFacade;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        // My code below. Above is stuff provided at the beginning.
        String baseUrl = "http://localhost:8080";
        ServerFacade serverFacade = new ServerFacade(baseUrl, null);
        ClientUI clientUI = new ClientUI(serverFacade);
        clientUI.run();
    }
}