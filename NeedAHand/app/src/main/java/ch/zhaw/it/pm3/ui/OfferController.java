package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.InvalidUserEntry;
import ch.zhaw.it.pm3.model.DocumentInfo;
import ch.zhaw.it.pm3.model.Offer;
import ch.zhaw.it.pm3.model.ServiceProvider;
import ch.zhaw.it.pm3.databaseHandling.DataManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * This class is the controller for the Offer window. It initializes the window when
 * opening it as well as for reacting to user inputs in the window, e. g.
 * clicking on buttons. The inputs will run through a validation process, where they will be
 * checked if everything is correct.
 * When the validation is correct a new offer will be created and added to a HashMap.
 */
public class OfferController implements ControlledScreens {
    private HashMap<String, Parent> screens = new HashMap<>();
    private Session session;
    private TextInputControl[] inputList;
    private String[] promptTexts;
    private DataManager dataManager = DataManager.getInstance();
    private boolean isServiceProvider;


    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField priceField;

    @FXML
    private AnchorPane root;

    @FXML
    private Button sendButton;

    @FXML
    private TextField streetAndNrField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private void initialize() {
        promptTexts = new String[]{firstnameField.getPromptText(), surnameField.getPromptText(), streetAndNrField.getPromptText(),
                postalCodeField.getPromptText(), titleField.getPromptText(), descriptionField.getPromptText(), priceField.getPromptText()};
        session = Session.getInstance();
        deactivateSendButton();
        numbersForPostalCodeAndPriceOnly();
        session.hasLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                isServiceProvider = session.getLoggedInUser() instanceof ServiceProvider;
                handleSwitchView();
                fillTextFields();
            }
        });
    }

    @FXML
    private void onGoBack(ActionEvent actionEvent) {
        root.getScene().setRoot(screens.get(Config.LIST_VIEW));
        session.setActualOfferIsSet(false);
    }

    @FXML
    private void onSubmit(ActionEvent actionEvent) {
        ServiceProvider serviceProvider = (ServiceProvider) session.getLoggedInUser();
        boolean validation = validationInput(serviceProvider);

        try {
            if (validation) {
                int advertisementId = session.getActualAdvertisement().getId();
                Offer newOffer = new Offer(getDocumentInfo(), dataManager.getNewId(dataManager.getOfferList(), Offer::getId), advertisementId, serviceProvider.getId());
                dataManager.addOfferToList(newOffer);
                root.getScene().setRoot(screens.get(Config.HOME));
                clearView();
                resetPromptTexts();
                resetBorderForFields();
            }
            throw new InvalidUserEntry("Invalid user entry!");
        } catch (InvalidUserEntry exception) {
            errorInfoInvalidEntry(firstnameField);
            errorInfoInvalidEntry(surnameField);
            errorInfoInvalidEntry(postalCodeField);
            errorInfoInvalidEntry(streetAndNrField);
        }
    }

    private void handleSwitchView() {
        if (!isServiceProvider) {
            sendButton.setVisible(false);
            session.actualOfferIsSetProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    fillTextFields();
                    disableFields();
                }
            });
        }
    }

    private void fillTextFields() {
        Offer actualOffer = session.getActualOffer();
        if (isServiceProvider) {
            ServiceProvider serviceProvider = (ServiceProvider) session.getLoggedInUser();
            firstnameField.setText(serviceProvider.getFirstName());
            surnameField.setText(serviceProvider.getSurname());
            streetAndNrField.setText(serviceProvider.getStreet());
            postalCodeField.setText(String.valueOf(serviceProvider.getPostalCode()));
        } else if (actualOffer != null) {
            titleField.setText(actualOffer.getTitle());
            descriptionField.setText(actualOffer.getDescription());
            firstnameField.setText(actualOffer.getFirstname());
            surnameField.setText(actualOffer.getSurname());
            streetAndNrField.setText(actualOffer.getStreet());
            postalCodeField.setText(String.valueOf(actualOffer.getPostalCode()));
            priceField.setText(String.valueOf(actualOffer.getPrice()));
        }
    }

    private DocumentInfo getDocumentInfo() {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.title = titleField.getText();
        documentInfo.description = descriptionField.getText();
        documentInfo.firstname = firstnameField.getText();
        documentInfo.surname = surnameField.getText();
        documentInfo.price = Double.parseDouble(priceField.getText());
        documentInfo.street = streetAndNrField.getText();
        documentInfo.postalCode = Integer.parseInt(postalCodeField.getText());

        return documentInfo;
    }

    private void disableFields() {
        titleField.setEditable(false);
        firstnameField.setEditable(false);
        surnameField.setEditable(false);
        streetAndNrField.setEditable(false);
        postalCodeField.setEditable(false);
        descriptionField.setEditable(false);
        priceField.setEditable(false);
    }

    private void numbersForPostalCodeAndPriceOnly() {
        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> { //allows the postalCodeField to only take Integers
            if (!newValue.matches("[0-9]{4}$")) {
                postalCodeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        priceField.textProperty().addListener((observable, oldValue, newValue) -> { //allows the postalCodeField to only take Integers
            if (!newValue.matches("\\d*")) {
                priceField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private boolean validationInput(ServiceProvider serviceProvider) {
        String firstname = firstnameField.getText();
        String surname = surnameField.getText();
        String streetAndNr = streetAndNrField.getText();
        int postalCode = Integer.parseInt(postalCodeField.getText());

        return firstname.equals(serviceProvider.getFirstName()) && surname.equals(serviceProvider.getSurname()) &&
                streetAndNr.equals(serviceProvider.getStreet()) && postalCode == serviceProvider.getPostalCode();
    }

    private void deactivateSendButton() {
        sendButton.disableProperty().bind(Bindings.isEmpty(titleField.textProperty())
                .or(Bindings.isEmpty(descriptionField.textProperty()))
                .or(Bindings.isEmpty(firstnameField.textProperty()))
                .or(Bindings.isEmpty(surnameField.textProperty()))
                .or(Bindings.isEmpty(streetAndNrField.textProperty()))
                .or(Bindings.isEmpty(postalCodeField.textProperty()))
                .or(Bindings.isEmpty(priceField.textProperty())));
    }

    private void createTextInputControlArray() {
        inputList = new TextInputControl[]{titleField, descriptionField, surnameField, firstnameField, streetAndNrField, postalCodeField, priceField};
    }

    private void clearView() {
        firstnameField.clear();
        surnameField.clear();
        titleField.clear();
        descriptionField.clear();
        priceField.clear();
        streetAndNrField.clear();
        postalCodeField.clear();
    }

    private void resetPromptTexts() {
        createTextInputControlArray();
        for (int i = 0; i < promptTexts.length; ++i) {
            inputList[i].clear();
            inputList[i].setPromptText(promptTexts[i]);
        }
    }

    private void resetBorderForFields() {
        firstnameField.setStyle("-fx-border-color: transparent");
        surnameField.setStyle("-fx-border-color: transparent");
        streetAndNrField.setStyle("-fx-border-color: transparent");
        postalCodeField.setStyle("-fx-border-color: transparent");
    }

    private static void errorInfoInvalidEntry(TextInputControl content) {
        content.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        content.setPromptText("fehlerhafte Eingabe!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }
}
