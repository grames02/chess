package ui;

import java.util.Locale;
import java.util.Scanner;
import ui.ServerFacade;

public class ClientUI {
    private final ServerFacade serverFacade;
    private Scanner input;
    private boolean loggedIn = false;

    public ClientUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.input = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            if (!loggedIn) {
                notLoggedInMenu();
            } else {
                loggedInMenu();
            }
        }
    }

    private void loggedInMenu() {
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
            System.out.print("\nYou will now be brought back to the main menu to make your selection.\n");
            System.out.print("\n");
        }

        else if (response.toLowerCase(Locale.ROOT).equals("login")) {
            System.out.print("\nPlease Enter your Login credentials with the following format:" +
                    "\n<USERNAME> <PASSWORD>\n");
            System.out.print("\nEnter Login Information HERE: ");
            String loginInfo = input.nextLine();
            String[] parts = loginInfo.trim().split(" ");
            if (parts.length != 2) {
                System.out.print("Invalid login entry, please try again.");
                return;
            }
            String username = parts[0];
            String password = parts[1];
            loginFunction(username, password);
        }

        else if (response.toLowerCase(Locale.ROOT).equals("register")) {
            System.out.print("\nTo register, please input your information with the following format:" +
                    "\n<USERNAME> <PASSWORD> <EMAIL>\n");
            System.out.print("\nEnter Registration Information HERE: ");
            String registration_info = input.nextLine();
        }

        else if (response.toLowerCase(Locale.ROOT).equals("quit")) {
            System.out.print("\nThank you for using our service, have a wonderful day!\n");
            return;
        }

        else {
            System.out.print("\nInvalid Response. Please Try Again.\n");
            System.out.print("\n");
        }
    }

    private void loginFunction(String username, String password) {
        try {
            var authorization = serverFacade.login(username, password);
            this.loggedIn = true;

        } catch (Exception e) {
            System.out.print("Login failed " + e.getMessage());
        }
    }
}
