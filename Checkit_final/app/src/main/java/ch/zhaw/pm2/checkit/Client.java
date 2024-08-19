package ch.zhaw.pm2.checkit;

import javafx.application.Application;

/**
 * Main class of the client, starting the client application.
 */
public class Client {

    public static void main(String[] args) {
        //Start UI
        System.out.println("Starting Client Application");
        Application.launch(ClientUI.class, args);
        System.out.println("Client Application ended");
    }
}
