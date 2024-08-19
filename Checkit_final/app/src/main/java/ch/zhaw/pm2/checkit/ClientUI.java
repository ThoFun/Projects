package ch.zhaw.pm2.checkit;

import ch.zhaw.pm2.checkit.client.ControlledScreens;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * The User interface of the client
 */
public class ClientUI extends Application {
    public static final String MAINVIEW = "Main";
    public static final String MAINVIEW_FILE = "MainWindow.fxml";
    public static final String NEWBOOKING = "NewBooking";
    public static final String NEWBOOKING_FILE = "BookingTemplate.fxml";

    private HashMap<String, Parent> screens = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        loadAllScreens();
        mainWindow(primaryStage);
    }

    /**
     * Loads the scene and adds it to the stage to be shown on the chat window.
     *
     * @param primaryStage to set the scene into
     */
    private void mainWindow(Stage primaryStage) {
        try {
            Scene scene = new Scene(screens.get(MAINVIEW));

            // configure and show stage
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(420);
            primaryStage.setMinHeight(250);
            primaryStage.setTitle("CheckIt.");
            primaryStage.show();
        } catch(Exception e) {
            System.err.println("Error starting up UI" + e.getMessage());
        }
    }

    private void loadAllScreens() {
        loadScreen(NEWBOOKING, NEWBOOKING_FILE);
        loadScreen(MAINVIEW, MAINVIEW_FILE);
    }

    private void loadScreen(String name, String fileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
            Parent loadScreen = loader.load();
            ControlledScreens controlledScreen = loader.getController();
            controlledScreen.setScreenList(screens);
            screens.put(name, loadScreen);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.getMessage();
        }
    }
}