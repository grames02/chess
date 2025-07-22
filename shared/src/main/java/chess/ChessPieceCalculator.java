package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ChessPieceCalculator {
    // I think what I want to do is have this file assess what the selected piece is. Then It'll assign
    // the possible movement style.
    private ChessPiece piece;
    private ChessPosition position;
    private ChessBoard board;
    
    public ChessPieceCalculator(ChessPiece piece, ChessPosition position, ChessBoard board) {
        this.piece = piece;
        this.position = position;
        this.board = board;
    }

    public Collection<ChessMove> pieceMoveset() {
        ChessPiece.PieceType type = piece.getPieceType();

        if (type == ChessPiece.PieceType.KING) {
            return kingMovement();
        }
        else if (type == ChessPiece.PieceType.QUEEN) {
            return queenMovement();
        }
        else if (type == ChessPiece.PieceType.ROOK) {
            return rookMovement();
        }
        else if (type == ChessPiece.PieceType.BISHOP) {
            return bishopMovement();
        }
        else if (type == ChessPiece.PieceType.KNIGHT) {
            return knightMovement();
        }
        else if (type == ChessPiece.PieceType.PAWN) {
            return pawnMovement();
        }

        return null;
        // Just in case

    }

    private Collection<ChessMove> kingMovement() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            if (withinBounds(newRow, newCol)) {
                ChessPosition newP = new ChessPosition(newRow, newCol);
                ChessPiece destPiece = board.getPiece(newP);

                // Checking to make sure we're good to move into there
                if (destPiece == null || destPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newP, null));
                }

            }
        }
        return moves;
    }





    private Collection<ChessMove> queenMovement() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            while (withinBounds(newRow, newCol)) {
                ChessPosition newP = new ChessPosition(newRow, newCol);
                ChessPiece destPiece = board.getPiece(newP);

                if (destPiece == null) {
                    moves.add(new ChessMove(position, newP, null));
                }
                else {
                    // This is if the piece it's interacting is of the opposite color,
                    // aka we're capturing it.
                    if (destPiece.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position,newP,null));
                    }
                    break;
                    // Now we're stopped for hitting one of our own pieces.
                }
            newRow += d[0];
            newCol += d[1];
            }
        }
        return moves;
    }





    private Collection<ChessMove> rookMovement() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                // Horizontal and Vertical
                {0,1},{0,-1},{1,0},{-1,0}
        };
        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            while (withinBounds(newRow, newCol)) {
                ChessPosition newP = new ChessPosition(newRow, newCol);
                ChessPiece destPiece = board.getPiece(newP);


                if (destPiece == null) {
                    moves.add(new ChessMove(position, newP, null));
                }
                else {
                    // This is if the piece it's interacting is of the opposite color,
                    // aka we're capturing it.
                    if (destPiece.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position,newP,null));
                    }
                    break;
                    // Now we're stopped for hitting one of our own pieces.
                }
            // Add to the direction
                newRow += d[0];
                newCol += d[1];
            }
        }
        return moves;
    }





    private Collection<ChessMove> bishopMovement() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
        };
        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            while (withinBounds(newRow, newCol)) {
                ChessPosition newP = new ChessPosition(newRow, newCol);
                ChessPiece destPiece = board.getPiece(newP);

                if (destPiece == null) {
                    moves.add(new ChessMove(position, newP, null));
                }
                else {
                    // This is if the piece it's interacting is of the opposite color,
                    // aka we're capturing it.
                    if (destPiece.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position,newP,null));
                    }
                    break;
                    // Now we're stopped for hitting one of our own pieces.
                }
            newRow += d[0];
            newCol += d[1];

            }
        }
        return moves;
    }





    private Collection<ChessMove> knightMovement() {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-2,-1}, {-2,1}, {-1, 2}, {-1, -2}, {2, -1}, {2, 1}, {1, 2}, {1, -2}
        };

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            if (withinBounds(newRow, newCol)) {
                ChessPosition newP = new ChessPosition(newRow, newCol);
                ChessPiece destPiece = board.getPiece(newP);

                // Checking to make sure we're good to move into there
                if (destPiece == null || destPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newP, null));
                }

            }
        }
        return moves;
    }




    private Collection<ChessMove> pawnMovement() {
        List<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int direction = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        int startingR = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promotionR = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 8 : 1;

        int newR = row + direction;
        if (withinBounds(newR, col)) {
            ChessPosition forward = new ChessPosition(newR, col);
            if (board.getPiece(forward) == null) {
                if (newR == promotionR) {
                    for (ChessPiece.PieceType promotype : new ChessPiece.PieceType[]{
                            ChessPiece.PieceType.QUEEN,
                            ChessPiece.PieceType.BISHOP,
                            ChessPiece.PieceType.KNIGHT,
                            ChessPiece.PieceType.ROOK}) {
                        moves.add(new ChessMove(position, forward, promotype));
                    }
                } else {
                    moves.add(new ChessMove(position, forward, null));
                    if (row == startingR) {
                        ChessPosition doubleF = new ChessPosition(row + 2 * direction, col);
                        if (withinBounds(doubleF.getRow(), doubleF.getColumn()) && board.getPiece(doubleF) == null) {
                            moves.add(new ChessMove(position, doubleF, null));
                        }
                    }
                }
            }
        }

        for (int i = -1; i <= 1; i += 2) {
            int newCol = col + i;
            int diagR = row + direction;
            if (!withinBounds(diagR, newCol)) {continue;}

            ChessPosition diag = new ChessPosition(diagR, newCol);
            ChessPiece destPiece = board.getPiece(diag);
            if (destPiece == null || destPiece.getTeamColor() == piece.getTeamColor()) {continue;}

            if (diagR == promotionR) {
                for (ChessPiece.PieceType promotype : new ChessPiece.PieceType[]{
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.BISHOP,
                        ChessPiece.PieceType.KNIGHT,
                        ChessPiece.PieceType.ROOK}) {
                    moves.add(new ChessMove(position, diag, promotype));
                }
            } else {
                moves.add(new ChessMove(position, diag, null));
            }
        }

        return moves;
    }



    private boolean withinBounds(int row, int col) {
        // Ensure that the rows and columns do not exceed or go below the scope of the board.
        return row >= 1 && row <=8 && col >= 1 && col <= 8;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPieceCalculator that = (ChessPieceCalculator) o;
        return Objects.equals(piece, that.piece) && Objects.equals(position, that.position) && Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, position, board);
    }
}
