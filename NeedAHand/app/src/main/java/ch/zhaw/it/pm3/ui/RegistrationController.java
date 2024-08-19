package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.model.*;
import ch.zhaw.it.pm3.databaseHandling.DataManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the controller for the Registration window. It initializes the window when
 * opening it as well as for reacting to user inputs in the window, e. g.
 * clicking on buttons. The inputs will run through a validation process, where they will be
 * checked if everything is correct.
 * When everything is correct a new Customer or ServiceProvider will be created.
 */
public class RegistrationController implements ControlledScreens {

    private static final String DATE_PATTERN = "^(1[0-2]|0[1-9])/[0-9]{4}$";
    private static final double STANDARD_RATING = 3.0;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private HashMap<String, Parent> screens = new HashMap<>();
    private Alert alert;
    private DataManager dataManager;

    @FXML
    private AnchorPane root;

    @FXML
    private CheckBox isAServiceProvider;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField mailAddressField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField passwordRepeatField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField companyNameField;

    @FXML
    private ComboBox<Skills> skillsComboBox;

    @FXML
    private TextField lengthExperienceField;

    @FXML
    private Label companyNameLabel;

    @FXML
    private Label lengthExperienceLabel;

    @FXML
    private Label areaOfExpertiseLabel;

    @FXML
    private Button registrationButton;

    @FXML
    void initialize() {
        dataManager = DataManager.getInstance();
        deactivateRegisterButton();
        setContentComboBox();
        toggleServiceProviderFields(false);
    }

    @FXML
    void onRegister(ActionEvent event) {
        UserInfo userInfo = initializeUserInfo();

        if (validateMailAddress(userInfo.mailAddress) && !isMailAddressTaken(userInfo.mailAddress) && passwordValidation(userInfo.password, passwordRepeatField.getText()) && dateValidation()) {
            User user;
            if (isAServiceProvider.isSelected()) {
                user = new ServiceProvider(userInfo, dataManager.getNewId(dataManager.getUserList(), User::getId), STANDARD_RATING);
            } else {
                user = new Customer(userInfo, dataManager.getNewId(dataManager.getUserList(), User::getId));
            }
            dataManager.addUser(user);
            clearFields();
            goToLoginView();
        }
    }

    @FXML
    void checkBoxIsCraftsman(ActionEvent event) {
        toggleServiceProviderFields(isAServiceProvider.isSelected());
    }

    private void toggleServiceProviderFields(boolean isAServiceProvider) {
        if (isAServiceProvider) {
            titleLabel.setText("Fachkraft");
            companyNameLabel.setVisible(true);
            areaOfExpertiseLabel.setVisible(true);
            lengthExperienceLabel.setVisible(true);
            companyNameField.setVisible(true);
            skillsComboBox.setVisible(true);
            lengthExperienceField.setVisible(true);
        } else {
            titleLabel.setText("Kunde");
            companyNameLabel.setVisible(false);
            areaOfExpertiseLabel.setVisible(false);
            lengthExperienceLabel.setVisible(false);
            companyNameField.setVisible(false);
            skillsComboBox.setVisible(false);
            lengthExperienceField.setVisible(false);
        }
    }

    private UserInfo initializeUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.firstname = firstnameField.getText();
        userInfo.surname = surnameField.getText();
        userInfo.mailAddress = mailAddressField.getText().toLowerCase();
        userInfo.password = passwordField.getText();
        userInfo.city = cityField.getText();
        userInfo.street = streetField.getText();
        userInfo.postalCode = Integer.parseInt(postalCodeField.getText());
        userInfo.companyName = companyNameField.getText();
        userInfo.skill = Skills.fromString(String.valueOf(skillsComboBox.getValue()));
        userInfo.lengthExperience = lengthExperienceField.getText();

        return userInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }

    @FXML
    void onGoBack(ActionEvent event) {
        root.getScene().setRoot(screens.get(Config.LOGIN));
    }

    /**
     * This method deactivates the registration-button when the TextFields are empty.
     * The button will only be activated when all the TextFields are filled in.
     **/
    private void deactivateRegisterButton() {
        registrationButton.disableProperty().bind(Bindings.isEmpty(mailAddressField.textProperty())
                .or(Bindings.isEmpty(firstnameField.textProperty()))
                .or(Bindings.isEmpty(surnameField.textProperty()))
                .or(Bindings.isEmpty(passwordField.textProperty()))
                .or(Bindings.isEmpty(passwordRepeatField.textProperty()))
                .or(Bindings.isEmpty(streetField.textProperty()))
                .or(Bindings.isEmpty(cityField.textProperty()))
                .or(Bindings.isEmpty(postalCodeField.textProperty())));
        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> { //allows the postalCodeField to only take Integers
            if (!newValue.matches("\\d*")) {
                postalCodeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * This method provides an fail-alert when the passwort is incorrect.
     */
    private void passwordValidationFailAlert() {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Inkorrekte Passwortangabe");
        alert.setContentText("Bitte geben Sie wieder das gleiche Passwort wie oben ein.");
        alert.showAndWait();
    }

    /**
     * This method checks if a mailAddress is already taken or not
     *
     * @param mailAddress provided from the user
     * @return true when mailAddress is not taken, else false
     */
    private boolean isMailAddressTaken(String mailAddress) {
        boolean isMailAddressTaken = false;
        for (User registeredUser : dataManager.getUserList()) {
            if (mailAddress.equals(registeredUser.getMailAddress())) {
                isMailAddressTaken = true;
                handleTakenMailAddress();
            }
        }
        return isMailAddressTaken;
    }

    /**
     * This method checks if the user provided the password correctly or not.
     *
     * @param passwordField       the password of the user that wants to register
     * @param passwordRepeatField repetition of the password that the user wants to use for the register
     * @return true when the passwords are the same, else false
     */
    private boolean passwordValidation(String passwordField, String passwordRepeatField) {
        if (!passwordField.equals(passwordRepeatField)) {
            passwordValidationFailAlert();
        }
        return passwordField.equals(passwordRepeatField);
    }

    /**
     * This method checks if the provided mailAddress is a valid mail-address.
     * For example: test@test.ch
     *
     * @param mailAddress provided by the user
     * @return true when the mail-address is valid, else false
     */
    private boolean validateMailAddress(String mailAddress) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mailAddress);
        if (!matcher.matches()) {
            mailValidationFailAlert();
        }
        return matcher.matches();
    }

    /**
     * This method provides an fail-alert when the mail-address is incorrect.
     */
    private void mailValidationFailAlert() {
        alert = new Alert(Alert.AlertType.ERROR, "Die angegebene Mailadresse ist falsch! Bitte korrigieren.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * This method provides an fail-alert when the dateformat is incorrect.
     */
    private void dateValidationFailAlert() {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Falsches Datenformat");
        alert.setContentText("Bitte geben Sie ihr Startdatum wie folgt ein: mm/yyyy");
        alert.showAndWait();
        lengthExperienceField.clear();
    }

    private boolean dateValidation() {
        if (!isAServiceProvider.isSelected()) {
            return true;
        }

        if (!lengthExperienceField.getText().matches(DATE_PATTERN)) {
            dateValidationFailAlert();
        }
        return lengthExperienceField.getText().matches(DATE_PATTERN);
    }

    private void goToLoginView() {
        root.getScene().setRoot(screens.get(Config.LOGIN));
    }

    private void handleTakenMailAddress() {
        alert = new Alert(Alert.AlertType.ERROR, "Mail-Adresse bereits verwendet!");
        alert.setHeaderText(null);
        alert.showAndWait();
        mailAddressField.clear();
    }

    private void clearFields() {
        firstnameField.clear();
        surnameField.clear();
        mailAddressField.clear();
        passwordField.clear();
        passwordRepeatField.clear();
        streetField.clear();
        cityField.clear();
        postalCodeField.clear();
        companyNameField.clear();
        skillsComboBox.setValue(null);
        lengthExperienceField.clear();
    }

    private void setContentComboBox() {
        skillsComboBox.getItems().setAll(Skills.values());
    }
}
