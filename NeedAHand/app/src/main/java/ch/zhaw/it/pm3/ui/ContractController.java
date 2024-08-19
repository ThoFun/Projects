package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.databaseHandling.DataManager;
import ch.zhaw.it.pm3.model.*;
import ch.zhaw.it.pm3.ui.actions.ControllerActions;
import ch.zhaw.it.pm3.ui.actions.CustomerActions;
import ch.zhaw.it.pm3.ui.actions.ServiceProviderActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is the controller for the Contract View. The text in the labels will be set differently depending on the current logged-in user.
 * A ServiceProvider will only see the contract and won't be able to interact with it.
 * A Customer will be able to sign the contract and close the Contract.
 */
public class ContractController implements ControlledScreens {
    private HashMap<String, Parent> screens = new HashMap<>();
    private Session session;
    private DataManager dataManager = DataManager.getInstance();
    private Offer offer;
    private Task task;
    private ServiceProvider serviceProvider;
    private ControllerActions controllerActions;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private AnchorPane root;

    @FXML
    private Label advertisementTitleLabel;

    @FXML
    private Label serviceProviderFirstNameLabel;

    @FXML
    private Label serviceProviderLocationLabel;

    @FXML
    private Label serviceProviderNameLabel;

    @FXML
    private Label serviceProviderPriceLabel;

    @FXML
    private Label serviceProviderRatingLabel;

    @FXML
    private Label acceptOfferLabel;

    @FXML
    private Button contractButton;

    @FXML
    void initialize() {
        session = Session.getInstance();
        descriptionTextArea.setEditable(false);
        session.hasLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (session.getLoggedInUser() instanceof ServiceProvider) {
                    controllerActions = new ServiceProviderActions();
                    serviceProvider = (ServiceProvider) session.getLoggedInUser();
                    offer = session.getActualOffer();
                } else {
                    controllerActions = new CustomerActions();
                }
                switchView();
            }
        });
        taskObserver();
        offerObserver();
    }

    @FXML
    void executeButton(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (!session.isActualTaskSet()) {
            alert.setTitle("Bestaetigung");
            alert.setHeaderText("Sind Sie sicher, dass Sie dieses Angebot annehmen wollen?");
            alert.showAndWait();
            if (alert.getResult().getText().equals("OK")) {
                createNewTask();
                session.setActualAdvertisementIsSet(false);
                session.setActualOfferIsSet(false);
                root.getScene().setRoot(screens.get(Config.HOME));
            }
        } else {
            taskClosed();
            popUpWindowBoxRating();
        }
    }

    @FXML
    void onGoBack(ActionEvent event) {
        session.setActualAdvertisementIsSet(false);
        session.setActualOfferIsSet(false);
        session.setActualTaskIsSet(false);
        controllerActions.onGoBack(root, screens, true, false);
    }

    private void setLabels() {
        if (session.isActualTaskSet()) {
            advertisementTitleLabel.setText(dataManager.getAdvertisementById(task.getAdvertisementId()).getTitle());
            serviceProviderNameLabel.setText(task.getSurname());
            serviceProviderFirstNameLabel.setText(task.getFirstname());
            serviceProviderLocationLabel.setText(task.getStreet());
            serviceProviderPriceLabel.setText(String.valueOf(task.getPrice()));
            serviceProviderRatingLabel.setText(String.valueOf(serviceProvider.getRating()));
            descriptionTextArea.setText(task.getDescription());
        } else {
            advertisementTitleLabel.setText(dataManager.getAdvertisementById(offer.getAdvertisementId()).getTitle());
            serviceProviderNameLabel.setText(offer.getSurname());
            serviceProviderFirstNameLabel.setText(offer.getFirstname());
            serviceProviderLocationLabel.setText(offer.getStreet());
            serviceProviderPriceLabel.setText(String.valueOf(offer.getPrice()));
            serviceProviderRatingLabel.setText(String.valueOf(serviceProvider.getRating()));
            descriptionTextArea.setText(offer.getDescription());
        }
    }

    private void switchView() {
        controllerActions.switchView(contractButton, acceptOfferLabel);
    }

    private void createNewTask() {
        Task newTask = new Task(dataManager.getNewId(dataManager.getTaskList(), Task::getTaskId), offer);
        dataManager.addTaskToList(newTask);
    }

    private void taskClosed() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Task abgeschlossen");
        alert.setHeaderText("Moechten Sie den Task abschliessen?");
        alert.showAndWait();
        if (alert.getResult().getText().equals("OK")) {
            task.setStatus(TaskStatus.DONE);
            root.getScene().setRoot(screens.get(Config.HOME));
        }
    }

    private void popUpWindowBoxRating() {
        AtomicReference<Double> serviceProviderRating = new AtomicReference<>((double) 0);
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        Label rating = new Label("Bewertung");
        Label ratingLabel = new Label("0.0");
        HBox hb = new HBox(20);
        hb.setStyle("-fx-background-color: white; -fx-padding: 20px;");
        hb.getChildren().addAll(rating, createSliderForRating(ratingLabel, serviceProviderRating), ratingLabel, createButtonForRating(stage, serviceProviderRating));
        stage.setScene(new Scene(hb));
        stage.setTitle("Bewertung");
        session.setActualTaskIsSet(false);
        stage.showAndWait();
    }

    private Button createButtonForRating(Stage stage, AtomicReference<Double> serviceProviderRating) {
        Button button = new Button("OK");
        button.setOnAction(event -> {
            stage.close();
            provideRatingForServiceProvider(serviceProviderRating.get());
        });

        return button;
    }

    private Slider createSliderForRating(Label ratingLabel, AtomicReference<Double> serviceProviderRating) {
        Slider slider = new Slider();
        slider.setMin(0.0);
        slider.setMax(5.0);
        slider.setValue(0.0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ratingLabel.setText(String.valueOf(newValue.doubleValue()).substring(0, 3));
            serviceProviderRating.set(Double.parseDouble(ratingLabel.getText().substring(0, 3)));
        });

        return slider;
    }

    private void provideRatingForServiceProvider(double rating) {
        serviceProvider = (ServiceProvider) dataManager.getUserById(serviceProvider.getId());
        serviceProvider.setRating(rating);
    }

    private void taskObserver() {
        session.actualTaskIsSetProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                task = session.getActualTask();
                serviceProvider = (ServiceProvider) dataManager.getUserById(task.getServiceProviderId());
                setLabels();
            }
        });
    }

    private void offerObserver() {
        session.actualOfferIsSetProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                offer = session.getActualOffer();
                serviceProvider = (ServiceProvider) dataManager.getUserById(offer.getServiceProviderId());
                setLabels();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }
}