package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.model.*;
import ch.zhaw.it.pm3.databaseHandling.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.util.HashMap;

/**
 * This class is the controller for the Advertisement View. It initializes the window when
 * opening it as well as for reacting to user inputs in the window, e.g.
 * clicking on buttons. The inputs will run through a validation process, where they will be
 * checked if everything is correct.
 */
public class AdvertisementController implements ControlledScreens {
    private HashMap<String, Parent> screens = new HashMap<>();
    private TextInputControl[] inputList;
    private String[] promptTexts;
    private DataManager dataManager = DataManager.getInstance();
    private Session session = Session.getInstance();
    private boolean isServiceProvider;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField streetField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private Button goBackButton;

    @FXML
    private Button sendButton;

    @FXML
    private Button offerButton;

    @FXML
    private ComboBox<Skills> skillsComboBox;

    @FXML
    void initialize() {
        promptTexts = new String[]{titleField.getPromptText(), descriptionField.getPromptText(), surnameField.getPromptText(),
                firstnameField.getPromptText(), streetField.getPromptText(), postalCodeField.getPromptText()};
        session.hasLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                isServiceProvider = session.getLoggedInUser() instanceof ServiceProvider;
                handleSwitchView();
            }
        });
        setContentComboBox();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }

    @FXML
    private void onGoBack(ActionEvent actionEvent) {
        if (isServiceProvider) {
            root.getScene().setRoot(screens.get(Config.LIST_VIEW));
            session.setActualAdvertisementIsSet(false);
        } else {
            root.getScene().setRoot(screens.get(Config.HOME));
        }
    }

    @FXML
    private void onSubmit(ActionEvent actionEvent) {
        boolean correctValidation = validationIsCorrect();

        if (correctValidation) {
            DocumentInfo documentInfo = initializeDocumentInfo();
            Customer customer = (Customer) session.getLoggedInUser();
            Advertisement newAdvertisement = new Advertisement(documentInfo, dataManager.getNewId(dataManager.getAdvertisementList(), Advertisement::getId), customer.getId());
            dataManager.addAdvertisementToList(newAdvertisement);
            resetView();
            root.getScene().setRoot(screens.get(Config.HOME));
        }
    }

    private boolean validationIsCorrect() {
        createTextInputControlArray();
        for (TextInputControl input : inputList) {
            if (input.getText().isEmpty()) {
                handleMissingTextInput(input);
                return false;
            }
            if (null == dueDatePicker.getValue() || LocalDate.now().isAfter(dueDatePicker.getValue())) {
                handleErrorDataInput();
                return false;
            }
        }
        return true;
    }

    private DocumentInfo initializeDocumentInfo() {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.title = titleField.getText();
        documentInfo.description = descriptionField.getText();
        documentInfo.date = dueDatePicker.getValue();
        documentInfo.firstname = firstnameField.getText();
        documentInfo.surname = surnameField.getText();
        documentInfo.street = streetField.getText();
        documentInfo.postalCode = Integer.parseInt(postalCodeField.getText());
        documentInfo.skill = skillsComboBox.getValue();

        return documentInfo;
    }

    private void createTextInputControlArray() {
        inputList = new TextInputControl[]{titleField, descriptionField, surnameField, firstnameField, streetField, postalCodeField};
    }

    private void handleMissingTextInput(TextInputControl textInputControl) {
        textInputControl.setPromptText("fehlerhafte Eingabe!");
    }

    private void handleErrorDataInput() {
        dueDatePicker.getEditor().setText("fehlerhafte Eingabe!");
    }

    private void resetView() {
        for (int i = 0; i < inputList.length; ++i) {
            inputList[i].clear();
            inputList[i].setPromptText(promptTexts[i]);
        }
        dueDatePicker.getEditor().clear();
    }

    private void handleSwitchView() {
        if (isServiceProvider) {
            sendButton.setVisible(false);

            session.actualAdvertisementIsSetProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    fillFields();
                    disableFields();
                }
            });
        } else {
            offerButton.setVisible(false);
        }
    }

    private void fillFields() {
        Advertisement actualAdvertisement = session.getActualAdvertisement();

        titleField.setText(actualAdvertisement.getTitle());
        firstnameField.setText(actualAdvertisement.getFirstname());
        surnameField.setText(actualAdvertisement.getSurname());
        streetField.setText(actualAdvertisement.getStreet());
        postalCodeField.setText(String.valueOf(actualAdvertisement.getPostalCode()));
        descriptionField.setText(actualAdvertisement.getDescription());
        dueDatePicker.setValue(actualAdvertisement.getDate());
        skillsComboBox.setValue(actualAdvertisement.getRequiredSkill());
    }

    private void disableFields() {
        titleField.setEditable(false);
        firstnameField.setEditable(false);
        surnameField.setEditable(false);
        streetField.setEditable(false);
        postalCodeField.setEditable(false);
        descriptionField.setEditable(false);
        dueDatePicker.setDisable(true);
        dueDatePicker.setOpacity(1);
        skillsComboBox.setDisable(true);
        skillsComboBox.setOpacity(1);
    }

    private void setContentComboBox() {
        skillsComboBox.getItems().setAll(Skills.values());
    }

    public void onOffer(ActionEvent actionEvent) {
        session.setActualAdvertisementIsSet(false);
        root.getScene().setRoot(screens.get(Config.OFFER));
    }
}
