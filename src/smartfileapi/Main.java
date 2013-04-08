package smartfileapi;

import java.util.Scanner;

public class Main {
    private static Scanner scan = new Scanner(System.in);

    // A simple method to ask the user a question and
    // return their response.
    private static String prompt(String prompt) {
         System.out.print(prompt);
         return scan.nextLine();
    }

    private static String API_URL = "http://vkovalenko.smartfile.com/api/1";
    private static String API_KEY = "fSEgN3SdzVvfwbOUuv2WUtLO4Gtrmj";
    private static String API_PWD = "06hKBxYpN3pHYRquOjlzbeki9kzXj2";

    // Get things started in main()
    public static void main(String[] args) {
        // Ask the user for the required parameters. These will be
        // passed to the API via an HTTP POST request.
         /*String fullname = prompt("Please enter a full name: ");
         String username = prompt("Please enter a username: ");
         String password = prompt("Please enter a password: ");
         String email = prompt("Please enter an email address: ");
         try {
             // Try to create the new user...
            SmartFileAPI.CreateUser(fullname, username, password, email);
            System.out.println(String.format("Successfully created user %s.", username));
         }
         catch (Exception e) {
             // Print the error message from the server on failure.
             System.out.println(String.format("Error creating User %s: %s.", username, e.toString()));
         }*/
        BasicClient api = new BasicClient(API_KEY,API_PWD);
    }

}
