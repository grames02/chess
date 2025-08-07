package chess;

import java.util.ArrayList;
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

    // Added fields for resignation and game over state
    private TeamColor resignedPlayer; // null if no resignation yet
    private boolean gameOver = false;

    public ChessGame() {
        currentTurn = TeamColor.WHITE;
        ChessBoard newBoard = new ChessBoard();
        newBoard.resetBoard();
        setBoard(newBoard);
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
        ChessPiece ourPiece = board.getPiece(startPosition);
        if (ourPiece == null) {
            return null;
        }
        Collection<ChessMove> moves = new ChessPieceCalculator(ourPiece, startPosition, board).pieceMoveset();
        Collection<ChessMove> approvedMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessBoard board2 = board.copy();
            ChessPiece movingPiece = board.getPiece(move.getStartPosition());

            board2.addPiece(move.getEndPosition(), movingPiece);
            board2.addPiece(move.getStartPosition(), null);
            if (move.getPromotionPiece() != null) {
                board2.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece()));
            }
            ChessGame testGame = new ChessGame();
            testGame.setBoard(board2);
            if (!testGame.isInCheck(movingPiece.getTeamColor())) {
                approvedMoves.add(move);
            }
        }
        return approvedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Prevent moves if game is over (e.g. resigned)
        if (gameOver) {
            throw new InvalidMoveException("Game is over. No moves allowed.");
        }

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        if (piece == null) {
            throw new InvalidMoveException("No piece!");
        }
        Collection<ChessMove> legalMoves = validMoves(start);
        if (!legalMoves.contains(move)) {
            throw new InvalidMoveException("Invalid movement.");
        }
        TeamColor color = piece.getTeamColor();
        ChessPiece endPiece = board.getPiece(end);
        if (endPiece != null) {
            if (endPiece.getTeamColor() == piece.getTeamColor()) {
                throw new InvalidMoveException("Same color");
            }
        }
        // Ensuring everything is within bounds.
        if (start.getRow() < 1 || start.getRow() > 8 || start.getColumn() < 1 || start.getColumn() > 8) {
            throw new InvalidMoveException("Start area is out of bounds");
        }
        if (end.getRow() < 1 || end.getRow() > 8 || end.getColumn() < 1 || end.getColumn() > 8) {
            throw new InvalidMoveException("End area is out of bounds");
        }
        // Tests for Pawns
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            // For when pawns go diagonally and there was no enemy in the corner.
            int valueColumn1 = start.getColumn();
            int valueColumn2 = end.getColumn();
            int columnResult = valueColumn2 - valueColumn1;
            if (columnResult == 1 || columnResult == -1) {
                if (board.getPiece(end) == null) {
                    throw new InvalidMoveException("No piece was in the corner. Pawn cannot move that way.");
                }
            }
            // For when pawns go more than 1, and they're not at the front line.
            if (start.getRow() != 2 && piece.getTeamColor() == TeamColor.WHITE) {
                int value1 = start.getRow();
                int value2 = end.getRow();
                int result = value2 - value1;

                if (result > 1) {
                    throw new InvalidMoveException("Pawn cannot go that far");
                }
            }
            if (start.getRow() == 2 && piece.getTeamColor() == TeamColor.WHITE) {
                int value1 = start.getRow();
                int value2 = end.getRow();
                int result = value2 - value1;
                if (result > 2) {
                    throw new InvalidMoveException("Pawn cannot go that far");
                }
            }
            if (start.getRow() != 7 && piece.getTeamColor() == TeamColor.BLACK) {
                int value1 = start.getRow();
                int value2 = end.getRow();
                int result = value1 - value2;

                if (result > 1) {
                    throw new InvalidMoveException("Pawn cannot go that far");
                }
            }
            if (start.getRow() == 7 && piece.getTeamColor() == TeamColor.BLACK) {
                int value1 = start.getRow();
                int value2 = end.getRow();
                int result = value1 - value2;

                if (result > 2) {
                    throw new InvalidMoveException("Pawn cannot go that far");
                }
            }
        }

        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Wrong team turn");
        }

        if (isInCheck(color)) {
            throw new InvalidMoveException("Going into check!");
        }

        board.addPiece(end, piece);
        board.addPiece(start, null);

        if (move.getPromotionPiece() != null) {
            board.addPiece(end, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }


        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Player resigns the game.
     * @param playerTeam the team resigning
     */
    public void resign(TeamColor playerTeam) {
        this.resignedPlayer = playerTeam;
        this.gameOver = true;
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
        kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            throw new IllegalStateException("No King was found");
        }

        // So now we have the King's position. Let's go ahead and check if the king is in danger. We'll scope out the board.
        // Doing this with a nested for loop. We'll check to see if the KIng is in danger from the opponents moveset.
        // from the opponent equals the King's position, then we know it is in check.
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pieceChecker = new ChessPosition(i, j);
                ChessPiece thatPiece = board.getPiece(pieceChecker);
                if (thatPiece != null && thatPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = new ChessPieceCalculator(thatPiece, pieceChecker, board).pieceMoveset();
                    if (kingChecker(moves, kingPosition)) {
                        return true;
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
        if (!isInCheck(teamColor)) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        if (isInCheck(TeamColor.BLACK) || isInCheck(TeamColor.WHITE)) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * @return the team that resigned or null if none
     */
    public TeamColor getResignedPlayer(boolean b) {
        return resignedPlayer;
    }

    /**
     * @return true if the game is over due to resignation or other reasons
     */
    public boolean isGameOver() {
        return gameOver;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row<=8; row++) {
            for (int col = 1; col<=8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        return null;
    }
    private boolean kingChecker(Collection<ChessMove> moves, ChessPosition kingPosition) {
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }
}
