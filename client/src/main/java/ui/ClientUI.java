package ui;

import model.AuthData;

import java.util.Locale;
import java.util.Scanner;

public class ClientUI {
    private final ServerFacade serverFacade;
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

            } else if (selection.toLowerCase(Locale.ROOT).equals("create game")) {

            } else if (selection.toLowerCase(Locale.ROOT).equals("list games")) {

            } else if (selection.toLowerCase(Locale.ROOT).equals("observe game")) {

            } else if (selection.toLowerCase(Locale.ROOT).equals("logout")) {
                loggedIn = false;

            } else if (selection.toLowerCase(Locale.ROOT).equals("help")) {
                System.out.print("\nWhen you are ready to return to the main menu, please press ENTER.\n");
                String done = input.nextLine();
                System.out.print("\n");
            } else {
                System.out.print("\nInvalid Response. Please Try Again.\n");
                System.out.print("\n");
            }
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
}
