package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.ListGamesResponse;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ClientUI {
    private final ServerFacade serverFacade;
    private List<GameData> currentGamesList;
    private Scanner input;
    private boolean loggedIn = false;
    private boolean quitProgram = false;
    private AuthData auth;

    public ClientUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.input = new Scanner(System.in);
    }

    public void run() {
        while (!quitProgram) {
            if (!loggedIn) {
                notLoggedInMenu();
            } else {
                loggedInMenu();
            }
        }
    }

    private void loggedInMenu() {
        while (loggedIn) {
            System.out.print("\nWelcome to Chess! Please enter one of the options listed below!\n" +
                    "\nPLAY GAME" + "\nCREATE GAME" + "\nLIST GAMES" +
                    "\nOBSERVE GAME" + "\nLOGOUT" + "\nHELP\n");
            System.out.print("\nEnter your selection here: ");
            String selection = input.nextLine();
            if (selection.toLowerCase(Locale.ROOT).equals("play game")) {
                System.out.print("\nPlease enter the game information with the following format:\n" +
                        "\n<Team Color: White/Black> <Game Number>\n");
                System.out.print("\nEnter Game Information HERE: ");
                String gameInformation = input.nextLine();
                String[] gameJoinParts = gameInformation.trim().split(" ");
                if (gameJoinParts.length != 2) {
                    System.out.print("Invalid game entry, please try again.");
                    return;
                }
                if (!gameJoinParts[0].toLowerCase(Locale.ROOT).equals("white") && !gameJoinParts[0].toLowerCase(Locale.ROOT).equals("black")) {
                    System.out.print("Invalid color entry, please try again");
                    return;
                }
                String playerColor = gameJoinParts[0];
                int gameId = Integer.parseInt(gameJoinParts[1]);
                joinGameFunction(playerColor, gameId);
                System.out.print("\nPress ENTER to return to the main menu.\n");
                input.nextLine();

            } else if (selection.toLowerCase(Locale.ROOT).equals("create game")) {
                System.out.print("\nPlease enter a name for the game:" +
                        "\n<GAME NAME>\n");
                System.out.print("\nEnter Game Name HERE: ");
                String gameName = input.nextLine();
                createGameFunction(gameName);

            } else if (selection.toLowerCase(Locale.ROOT).equals("list games")) {
                System.out.print("\nHere are the current chess games:\n");
                listChessGames();
                // Entering this below so that the user can see the games for a minute before
                // moving on to the next selection.
                System.out.print("\nWhen you are ready to return to the main menu, please press ENTER.\n");
                String done = input.nextLine();
                System.out.print("\n");
            }
            else if (selection.toLowerCase(Locale.ROOT).equals("observe game")) {
                System.out.print("\nPlease enter the game number to observe:\n");
                System.out.print("\nEnter Game Number HERE: ");
                String gameIdStr = input.nextLine();
                int gameId;
                try {
                    gameId = Integer.parseInt(gameIdStr);
                } catch (NumberFormatException e) {
                    System.out.print("Invalid game number. Please enter a valid number.\n");
                    return;
                }
                observeGameFunction(gameId);

                

            } else if (selection.toLowerCase(Locale.ROOT).equals("logout")) {
                System.out.print("\nYou are now logged out. Sending you back to the home menu.\n");
                loggedOutFunction();
                loggedIn = false;

            } else if (selection.toLowerCase(Locale.ROOT).equals("help")) {
                System.out.print("\nYou have selected: HELP.\nHere are further details regarding what each command does:\n");
                System.out.print("\nPLAY GAME - Select to enter an existing game of chess.\n" +
                        "CREATE GAME - Select to create a new game of chess.\n" +
                        "LIST GAMES - Select to see all the current games of chess and their participants.\n" +
                        "OBSERVE GAME - Select to watch a current game of chess.\n" +
                        "LOGOUT - Select to logout of your account and return to the home menu.\n" +
                        "HELP - Select to display these options, as you've so wonderfully done!\n");
                System.out.print("\nWhen you are ready to return to the main menu, please press ENTER.\n");
                String done = input.nextLine();
                System.out.print("\n");
            } else {
                System.out.print("\nInvalid Response. Please Try Again.\n");
                System.out.print("\n");
            }
        }
    }

    private void observeGameFunction(int gameId) {
            try {
                char[][] boardState = serverFacade.observeGame(auth.authToken(), gameId);
                boolean fromWhitePerspective = true;
                ChessBoardDrawer.drawBoard(boardState, fromWhitePerspective);
                System.out.print("\nPress ENTER to return to the main menu.\n");
                input.nextLine();
            } catch (Exception e) {
                System.out.print("Failed to observe game: " + e.getMessage() + "\n");
        }

    }

    private void notLoggedInMenu() {
        System.out.print("Welcome to Chess! Please enter one of the options listed below!" +
                "\nLOGIN\n" + "REGISTER\n" + "QUIT\n" + "HELP\n");
        System.out.print("\nEnter your selection here: ");
        String response = input.nextLine();

        if (response.toLowerCase(Locale.ROOT).equals("help")) {
            System.out.print("\nYou have selected: HELP.\nHere are further details regarding what each command does:\n");
            System.out.print("\nLOGIN - To enter as an existing user.\n" +
                    "REGISTER - To create your user account\n" +
                    "QUIT - To exit the game. We hope to see you again soon!\n" +
                    "HELP - Display these options, as you've so wonderfully done!\n");
            System.out.print("\nWhen you are ready to return to the main menu, please press ENTER.\n");
            String done = input.nextLine();
            System.out.print("\n");
        }

        else if (response.toLowerCase(Locale.ROOT).equals("login")) {
            System.out.print("\nPlease Enter your Login credentials with the following format:" +
                    "\n<USERNAME> <PASSWORD>\n");
            System.out.print("\nEnter Login Information HERE: ");
            String loginInfo = input.nextLine();
            String[] loginParts = loginInfo.trim().split(" ");
            if (loginParts.length != 2) {
                System.out.print("Invalid login entry, please try again.");
                return;
            }
            String username = loginParts[0];
            String password = loginParts[1];
            loginFunction(username, password);
        }

        else if (response.toLowerCase(Locale.ROOT).equals("register")) {
            System.out.print("\nTo register, please input your information with the following format:" +
                    "\n<USERNAME> <PASSWORD> <EMAIL>\n");
            System.out.print("\nEnter Registration Information HERE: ");
            String registrationInfo = input.nextLine();
            String[] registerParts = registrationInfo.trim().split(" ");
            if (registerParts.length != 3) {
                System.out.print("Invalid registration entry, please try again");
                return;
            }
            String username = registerParts[0];
            String password = registerParts[1];
            String email = registerParts[2];
            registerFunction(username, password, email);
        }

        else if (response.toLowerCase(Locale.ROOT).equals("quit")) {
            System.out.print("\nThank you for using our service, have a wonderful day!\n");
            quitProgram = true;
        }

        else {
            System.out.print("\nInvalid Response. Please Try Again.\n");
            System.out.print("\n");
        }
    }

    private void registerFunction(String username, String password, String email) {
        try {
            this.auth = serverFacade.register(username, password, email);
            this.loggedIn = true;
        } catch (Exception e) {
            System.out.print("Registration failed " + e.getMessage());
        }
    }

    private void loginFunction(String username, String password) {
        try {
            this.auth = serverFacade.login(username, password);
            this.loggedIn = true;
        } catch (Exception e) {
            System.out.print("Login failed " + e.getMessage());
        }
    }


    private void joinGameFunction(String playerColor, int gameId) {
        try {
            serverFacade.joinGame(auth.authToken(), playerColor, gameId);
            drawChessBoard(playerColor);
        } catch (Exception e) {
            System.out.print("Joining Game Failed " + e.getMessage());
        }
    }

    private void drawChessBoard(String playerColor) {
        char[][] startingBoard = {
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        boolean fromWhitePerspective = playerColor.equalsIgnoreCase(ChessGame.TeamColor.WHITE.toString());
        ChessBoardDrawer.drawBoard(startingBoard, fromWhitePerspective);
    }


    private void listChessGames() {
        try {
            ListGamesResponse gamesResponse = serverFacade.listGames(auth.authToken());
            List<GameData> games = gamesResponse.getGames();
            if (games.isEmpty()) {
                System.out.print("No games found.");
            } else {
                int numberedList = 1;
                for (GameData game: games) {
                    System.out.printf("%d. Game Name: %s, White: %s, Black: %s%n",
                            numberedList,
                            game.gameName(),
                            game.whiteUsername() != null ? game.whiteUsername() : "No player",
                            game.blackUsername() != null ? game.blackUsername() : "No player");
                    numberedList++;
                }

            }        } catch (Exception e) {
            System.out.print("Listing Games Failed " + e.getMessage());
        }
    }

    private void loggedOutFunction() {
        try {
            serverFacade.logout(auth.authToken());
        } catch (Exception e) {
            System.out.print("Logout failed " + e.getMessage());
        }
    }

    private void createGameFunction(String gameName) {
        try {
            serverFacade.createGame(gameName, auth.authToken());
        } catch (Exception e) {
            System.out.print("Game Creation Failed " + e.getMessage());
        }
    }

}
