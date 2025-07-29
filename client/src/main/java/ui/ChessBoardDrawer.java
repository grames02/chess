package ui;

public class ChessBoardDrawer {
    public static void drawBoard(char[][] board, boolean fromWhitePerspective) {
        if (board.length != 8 || board[0].length != 8) {
            throw new IllegalArgumentException("Board must be 8x8");
        }
        String letters = "abcdefgh";
        String numbers = "12345678";

        System.out.print(EscapeSequences.ERASE_SCREEN);

        System.out.print("   ");
        for (int col = 0; col < 8; col++) {
            int fileIndex = fromWhitePerspective ? col : 7 - col;
            System.out.print(" " + letters.charAt(fileIndex) + "  ");
        }
        System.out.println();

        for (int row = 0; row < 8; row++) {
            int boardRow = fromWhitePerspective ? 7 - row : row;

            System.out.print(" " + numbers.charAt(boardRow) + " ");

            for (int col = 0; col < 8; col++) {
                int boardCol = fromWhitePerspective ? col : 7 - col;

                boolean isLightSquare = (boardRow + boardCol) % 2 == 0;
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                String pieceSymbol = getPieceSymbol(board[boardRow][boardCol]);
                System.out.print(bgColor + pieceSymbol + EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + numbers.charAt(boardRow) + " ");
        }

        System.out.print("   ");
        for (int col = 0; col < 8; col++) {
            int fileIndex = fromWhitePerspective ? col : 7 - col;
            System.out.print(" " + letters.charAt(fileIndex) + "  ");
        }
        System.out.println();
    }

    private static String getPieceSymbol(char c) {
        return switch (c) {
            case 'K' -> EscapeSequences.WHITE_KING;
            case 'k' -> EscapeSequences.BLACK_KING;
            case 'Q' -> EscapeSequences.WHITE_QUEEN;
            case 'q' -> EscapeSequences.BLACK_QUEEN;
            case 'B' -> EscapeSequences.WHITE_BISHOP;
            case 'b' -> EscapeSequences.BLACK_BISHOP;
            case 'N' -> EscapeSequences.WHITE_KNIGHT;
            case 'n' -> EscapeSequences.BLACK_KNIGHT;
            case 'P' -> EscapeSequences.WHITE_PAWN;
            case 'p' -> EscapeSequences.BLACK_PAWN;
            case 'R' -> EscapeSequences.WHITE_ROOK;
            case 'r' -> EscapeSequences.BLACK_ROOK;
            default -> EscapeSequences.EMPTY;
        };
    }
}
