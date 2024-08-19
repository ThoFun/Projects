package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.InvalidUserEntry;
import ch.zhaw.it.pm3.databaseHandling.DataManager;
import ch.zhaw.it.pm3.model.User;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * This class is the controller for the Login window. A Customer or ServiceProvider will be asked to log in.
 * When the login is successful, the Customer or ServiceProvider will be brought to their Home-views and will be set as loggedInUser.
 */
public class LoginController implements ControlledScreens {
    private HashMap<String, Parent> screens = new HashMap<>();
    private DataManager dataManager;
    private Session session;

    @FXML
    private TextField mailAddressField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private AnchorPane root;
    @FXML
    private Button loginButton;

    /**
     * gets the {@link DataManager} instance and {@link Session} instance.
     * it also deactivates the button when needed fields are empty.
     */
    @FXML
    void initialize() {
        dataManager = DataManager.getInstance();
        session = Session.getInstance();
        loginButton.disableProperty().bind(Bindings.isEmpty(mailAddressField.textProperty())
                .or(Bindings.isEmpty(passwordField.textProperty())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }

    @FXML
    private void onLoginUser() {
        try {
            String mailAddress = mailAddressField.getText();
            String password = passwordField.getText();
            for (User user : dataManager.getUserList()) {
                if (user.getMailAddress().equals(mailAddress) && user.getPassword().equals(password)) {
                    session.setLoggedInUser(user);
                    goToHomeView();
                    return;
                }
            }
            throw new InvalidUserEntry("Invalid user entry!");

        } catch (InvalidUserEntry exception) {
            errorInfoInvalidEntry(Color.RED, mailAddressField);
            errorInfoInvalidEntry(Color.RED, passwordField);
        }
    }

    @FXML
    private void onGoToRegistrationView() {
        root.getScene().setRoot(screens.get(Config.REGISTRATION));
    }

    private void goToHomeView() {
            root.getScene().setRoot(screens.get(Config.HOME));
    }

    private static void errorInfoInvalidEntry(Color color, TextInputControl content) {
        content.clear();
        content.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        content.setPromptText("fehlerhafte Eingabe!");
    }
}
