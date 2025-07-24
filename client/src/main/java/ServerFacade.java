import java.util.Locale;
import java.util.Scanner;

public class ServerFacade {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        while (true) {
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
                String login_info = input.nextLine();
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


    }
}
