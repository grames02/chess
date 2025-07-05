package chess;

import java.util.Collection;
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
    private int turncounter;

    public ChessGame() {
        setBoard(board);
        turncounter = 1;
        getTeamTurn();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (turncounter == 1) {
            // White's Turn
            turncounter = 0;
            return TeamColor.WHITE;
        } else {
            turncounter = 1;
            return TeamColor.BLACK;
        }
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (turncounter == 1) {
            // White's Turn
            turncounter = 0;
            team = TeamColor.WHITE;
        } else {
            turncounter = 1;
            team = TeamColor.BLACK;
        }
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
        // First off we want to look at which team we have. That value is stored within: teamColor.
        // Afterwards. We're going to find the King and see if he is in Danger. We could look at the Valid moves.
        // Then, we could strike off moves that'll put the King into check and return the new list of values.
        int row = 1;
        int col = 1;
        ChessPosition our_king_spot = new ChessPosition(row, col);
        ChessPiece our_eventual_king = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
        while (our_eventual_king.getPieceType() != ChessPiece.PieceType.KING) {
            ChessPiece current_piece = board.getPiece(our_king_spot);
            if (current_piece.getPieceType() == ChessPiece.PieceType.KING) {
                our_eventual_king = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
            } else {
                if (row == 8) {
                    row = 1;
                    col += 1;
                } else {
                    row += 1;
                    our_king_spot = new ChessPosition(row, col);
                }
            }
        }
        //So now we have the King's position. Let's go ahead and check if the king is in danger. We could use a while loop.
        // This time we'll check for the other team's color and then run the Valid moves function. If one of the valid moves
        // from the opponent equals the King's position, then we know it is in check.

    return true;
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
        new ChessBoard().resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        this.board = board;
        return board;
    }
}

