package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {
        currentTurn = TeamColor.WHITE;
        ChessBoard new_board = new ChessBoard();
        setBoard(new_board);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        isInCheck(currentTurn);
        ChessPiece our_piece = board.getPiece(startPosition);
            return new ChessPieceCalculator(our_piece, startPosition, board).piece_moveset();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // We're going to find the King by looking throughout the board.
        int row;
        int col;
        ChessPosition kingPosition = null;
        outer:
        for (row = 1; row <= 8; row++) {
            for (col = 1; col <= 8; col++) {
                ChessPosition potential_king_spot = new ChessPosition(row, col);
                ChessPiece our_eventual_king = board.getPiece(potential_king_spot);

                if (our_eventual_king != null && our_eventual_king.getPieceType() == ChessPiece.PieceType.KING && our_eventual_king.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(row, col);
                    break outer;
                }
            }
        }
        if (kingPosition == null) {
            return false;
        }

        // So now we have the King's position. Let's go ahead and check if the king is in danger. We'll scope out the board.
        // Doing this with a nested for loop. We'll check to see if the KIng is in danger from the opponents moveset.
        // from the opponent equals the King's position, then we know it is in check.
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition piece_checker = new ChessPosition(i, j);
                ChessPiece that_piece = board.getPiece(piece_checker);
                if (that_piece != null && that_piece.getTeamColor() != teamColor) {
                    List<ChessMove> moves = (List<ChessMove>) new ChessPieceCalculator(that_piece, piece_checker, board).piece_moveset();
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        // Means the King isn't in danger.
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        this.board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

}