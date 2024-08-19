package ch.zhaw.pm2.checkit.client;

import ch.zhaw.pm2.checkit.ClientUI;
import ch.zhaw.pm2.checkit.protocol.Booking;
import ch.zhaw.pm2.checkit.protocol.Mail;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the booking window. Provides for initializing the window when
 * opening it as well as for reacting to user inputs in the window, e. g.
 * clicking on buttons.
 */
public class NewBookingController implements ControlledScreens {
    private final BookingManager bookingManager = new BookingManager(new ClientConnectionHandler());
    private final int MIN_AMOUNT_OF_NIGHTS = 1;
    private final int MAX_AMOUNT_OF_NIGHTS = 99;
    private HashMap<String, Parent> screens = new HashMap<>();
    private Booking booking;
    private Alert alert;
    @FXML
    private DatePicker endtime;

    @FXML
    private TextField firstname;

    @FXML
    private TextField lastname;

    @FXML
    private TextField mailAddress;

    @FXML
    private DatePicker starttime;

    @FXML
    private Spinner<Integer> amountOfNight;

    @FXML
    private AnchorPane root;

    @FXML
    void initialize() {
        amountOfNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_AMOUNT_OF_NIGHTS, MAX_AMOUNT_OF_NIGHTS));
        endtime.setDayCellFactory(disableDates());
        starttime.setDayCellFactory(disableDates());
        amountOfNight.valueProperty().addListener(e -> {
            starttime.setDayCellFactory(disableDates());
            endtime.setDayCellFactory(disableDates());
        });
    }

    @FXML
    void acceptDialog() {
        String firstName = firstname.getText();
        String lastName = lastname.getText();
        LocalDate startTime = starttime.getValue();
        LocalDate endTime = endtime.getValue();
        String mail = mailAddress.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || endTime == null || startTime == null || mail.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR, "Please fill in the blank spaces!");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if (!Mail.validateAddress(mail)) {
            mailValidationFailAlert();
        } else {
            booking = new Booking(firstName, lastName, mail, startTime, endTime);
            if (bookingManager.safeBooking(booking)) {
                confirmationAlert();
                root.getScene().setRoot(screens.get(ClientUI.MAINVIEW));
            } else {
                createBookingFailAlert();
            }
        }
    }

    @FXML
    void cancelDialog() {
        resetForm();
        root.getScene().setRoot(screens.get(ClientUI.MAINVIEW));
    }

    @FXML
    void selectEndTime() {
        if (endtime.getValue().isBefore(starttime.getValue().plusDays(1))) {
            createInvalidDatesAlert();
            endtime.setValue(null);
        }
    }

    private void resetForm() {
        firstname.clear();
        lastname.clear();
        mailAddress.clear();
    }

    private void mailValidationFailAlert() {
        alert = new Alert(Alert.AlertType.ERROR, "Provided mail-address is incorrect! Please adjust.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void confirmationAlert() {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Booking successful!\n" +
                "\n" +
                "Your Booking-Code: " + booking.getId());
        alert.setContentText("Please save it in a File or write it down on a piece of paper.\n" +
                "If you lose your code, please contact the front-desk.");
        alert.showAndWait();
        resetForm();
    }

    private void createBookingFailAlert() {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Booking not successful!\n");
        alert.setContentText("Something went wrong with creating the booking.");
        alert.showAndWait();
        resetForm();
    }

    private void createInvalidDatesAlert() {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid dates");
        alert.setContentText("The starttime needs to be before the endtime.");
        alert.showAndWait();
        resetForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }

    /**
     * Callback method to disable already reserved date-cells, based on preferred amount of nights
     *
     * @return Callback
     */
    public Callback<DatePicker, DateCell> disableDates() {
        Map<LocalDate, LocalDate> freeSlots = bookingManager.getAvailableTimeSlots(amountOfNight.getValue());

        return new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (dateIsInvalid(item)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #565c66");
                        } else {
                            setDisable(false);
                        }
                    }

                    private boolean dateIsInvalid(LocalDate date) {
                        for (Map.Entry<LocalDate, LocalDate> availableTime : freeSlots.entrySet()) {
                            if (date.isBefore(availableTime.getValue().plusDays(1)) && date.isAfter(availableTime.getKey().minusDays(1))) {
                                return false;
                            }
                        }
                        return true;
                    }
                };
            }
        };
    }
}
