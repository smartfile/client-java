package smartfileapi;

import oauth.signpost.OAuth;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.

public class Main {

    private static String API_URL = "https://vkovalenko.smartfile.com/api/1";
    private static String API_KEY = "fSEgN3SdzVvfwbOUuv2WUtLO4Gtrmj";
    private static String API_PWD = "06hKBxYpN3pHYRquOjlzbeki9kzXj2";
    private static String CLIENT_TOKEN = "9rOTRK6FjgUACxEVQW2xyxawBVMpwJ";
    private static String CLIENT_SECRET = "xOdAVYcC6H8SmJcEX9fqDQ7K2k6Ws4";
    private static Scanner scan = new Scanner(System.in);

    // A simple method to ask the user a question and
    // return their response.
    private static String prompt(String prompt) {
        System.out.print(prompt);
        return scan.nextLine();
    }


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
        try {

            BasicClient api = new BasicClient(API_KEY,API_PWD);
            InputStream stream = (InputStream)api.get("/ping");
            StringWriter writer = new StringWriter();
            System.out.println((InputStream)api.get("/ping"));
            //System.out.println((String)api.get("/path/data/Firefox_wallpaper.png"));


        } catch (SmartFileException e) {
            System.out.println(e.toString());
            return ;
        }
    }

}
