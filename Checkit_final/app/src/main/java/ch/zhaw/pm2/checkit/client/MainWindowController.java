package ch.zhaw.pm2.checkit.client;

import ch.zhaw.pm2.checkit.ClientUI;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;

/**
 * Controller for the booking window
 */
public class MainWindowController implements ControlledScreens {
    private final BookingManager bookingManager = new BookingManager(new ClientConnectionHandler());
    private HashMap<String, Parent> screens;
    private Alert alert;

    @FXML
    private TextField bookcode;

    @FXML
    private TextField checkOutCode;

    @FXML
    private Button createBooking;

    @FXML
    private AnchorPane root;

    @FXML
    private Button sendCodeButton;

    @FXML
    void createBooking() {
        root.getScene().setRoot(screens.get(ClientUI.NEWBOOKING));
    }

    @FXML
    void checkIn() {
        try {
            if (bookingManager.checkIn(Long.parseLong(bookcode.getText()))) {
                successfulCheckInAlert();
            } else {
                failedCheckInAlert();
            }
        } catch (NumberFormatException e) {
            invalidBookingNumberAlert();
        }
    }

    @FXML
    void checkOut() {
        try {
            if (bookingManager.checkOut(Long.parseLong(checkOutCode.getText()))) {
                successfulCheckOutAlert();
            } else {
                failedCheckOutAlert();
            }
        } catch (NumberFormatException e) {
            invalidBookingNumberAlert();
        }
    }

    private void failedCheckInAlert() {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Checkin wasn't possible!\n");
        alert.setContentText("Please try again, if the problem remains, ask the front desk.");
        alert.showAndWait();
    }

    private void failedCheckOutAlert() {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Checkout wasn't possible!\n");
        alert.setContentText("Please try again, if the problem remains, ask the front desk.");
        alert.showAndWait();
    }

    private void invalidBookingNumberAlert() {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid booking code!\n");
        alert.setContentText("Please try again");
        alert.showAndWait();
    }

    private void successfulCheckInAlert() {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Checkin successful!\n");
        alert.setContentText("Now load a keycard with the roomkey");
        alert.showAndWait();
    }

    private void successfulCheckOutAlert() {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Checkout successful!\n");
        alert.setContentText("Now throw the keycard with the roomkey in the basket");
        alert.showAndWait();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }
}