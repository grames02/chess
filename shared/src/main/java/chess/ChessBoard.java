package chess;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        // This will give us a basic setup chessboard.

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    // Adding in a copy board method. This will allow us to validate moves in the main game file.
    public ChessBoard copy() {
        ChessBoard copy = new ChessBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition spot = new ChessPosition(i,j);
                ChessPiece piece = this.getPiece(spot);
                if (piece != null) {
                    copy.addPiece(spot, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                }
            }
        }
        return copy;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Right here, this will set up all the pieces onto the Chess Board.
        // This loop will go through and for each number, setup all of those pieces.
                for (int j = 0; j <= 5; j++) {
                    if (j == 0) {
                    // Pawns
                        ChessPiece white_p = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                        ChessPiece black_p = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                        // We will use a nested for loop to produce all the pawns
                        for (int i = 0; i <= 1; i++) {
                                for (int k = 1; k <= 8; k++) {
                                    if (i == 0) {
                                        // We are setting up the white pawns.
                                        int row = 2;
                                        ChessPosition position = new ChessPosition(row, k);
                                        addPiece(position, white_p);
                                    }

                                    else {
                                        // We are setting up the black pawns.
                                        int row = 7;
                                        ChessPosition position = new ChessPosition(row, k);
                                        addPiece(position, black_p);
                                    }
                                }
                            }
                        }
                    else if (j == 1) {
                    // Knights
                        ChessPiece white_n = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                        ChessPosition position = new ChessPosition(1, 2);
                        ChessPosition position2 = new ChessPosition(1, 7);
                        addPiece(position, white_n);
                        addPiece(position2, white_n);

                        ChessPiece black_n = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                        ChessPosition position3 = new ChessPosition(8, 2);
                        ChessPosition position4 = new ChessPosition(8, 7);
                        addPiece(position3, black_n);
                        addPiece(position4, black_n);

                    }
                    else if (j == 2) {
                    // Rooks
                        ChessPiece white_r = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                        ChessPosition position = new ChessPosition(1,1);
                        ChessPosition position2 = new ChessPosition(1,8);
                        addPiece(position, white_r);
                        addPiece(position2, white_r);

                        ChessPiece black_r = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                        ChessPosition position3 = new ChessPosition(8,1);
                        ChessPosition position4 = new ChessPosition(8,8);
                        addPiece(position3, black_r);
                        addPiece(position4, black_r);
                    }
                    else if (j == 3) {
                    // Bishops
                        ChessPiece white_b = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                        ChessPosition position = new ChessPosition(1,3);
                        ChessPosition position2 = new ChessPosition(1,6);
                        addPiece(position, white_b);
                        addPiece(position2, white_b);

                        ChessPiece black_b = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                        ChessPosition position3 = new ChessPosition(8,3);
                        ChessPosition position4 = new ChessPosition(8,6);
                        addPiece(position3, black_b);
                        addPiece(position4, black_b);
                    }
                    else if (j == 4) {
                    // Kings
                        ChessPiece white_k = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                        ChessPosition position = new ChessPosition(1,5);
                        addPiece(position, white_k);

                        ChessPiece black_k = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                        ChessPosition position2 = new ChessPosition(8,5);
                        addPiece(position2, black_k);
                    }
                    else if (j == 5) {
                    // Queens
                        ChessPiece white_q = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                        ChessPosition position = new ChessPosition(1,4);
                        addPiece(position, white_q);

                        ChessPiece black_q = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                        ChessPosition position2 = new ChessPosition(8,4);
                        addPiece(position2, black_q);
                    }
                }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }



}
