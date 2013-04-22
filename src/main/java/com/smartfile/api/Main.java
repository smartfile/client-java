package com.smartfile.api;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static String API_URL = "https://vkovalenko.smartfile.com/api/1";
    private static String API_KEY = "a3bd0mwRRErcIutlS2hfhAGXYHjMlb";
    private static String API_PWD = "FV59nQWoZ0MQzTC3ahSTIRPB3lTSvS";
    private static String CLIENT_TOKEN = "EUGFYytlgvd0Icnga0yCV1zSPxKwut";
    private static String CLIENT_SECRET = "B8mHPp2jsGPVyN0KeDlUFj8AkZlcjf";
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

            //BasicClient api = new BasicClient(API_KEY,API_PWD);
            OAuthClient api = new OAuthClient(CLIENT_TOKEN,CLIENT_SECRET);
            api.setApiUrl("xxxx.ru");
            api.get_request_token();
            System.out.println(api.get_authorization_url());
            api.get_access_token(prompt("Enter verifier: "));
            System.out.println(IOUtils.toString(api.get("/ping")));
            System.out.println(IOUtils.toString(api.get("/path/info")));
            //System.out.println((String)api.get("/path/data/Firefox_wallpaper.png"));


        } catch (SmartFileException e) {
            System.out.println(e.toString());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

}
