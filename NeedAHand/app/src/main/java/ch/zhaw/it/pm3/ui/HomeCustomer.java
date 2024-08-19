package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.model.ServiceProvider;
import ch.zhaw.it.pm3.ui.actions.ControllerActions;
import ch.zhaw.it.pm3.ui.actions.CustomerActions;
import ch.zhaw.it.pm3.ui.actions.ServiceProviderActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;

/**
 * This class is the controller of the Home window from the Customer.
 * Here the Customer can choose to go to his userprofile or to create an Emergency Advertisement.
 */
public class HomeCustomer implements ControlledScreens {
    private HashMap<String, Parent> screens = new HashMap<>();
    private Session session;
    private ControllerActions controllerActions;

    @FXML
    private AnchorPane root;
    @FXML
    private Button mainButton;
    @FXML
    private Button secondButton;

    @FXML
    void initialize() {
        session = Session.getInstance();
        session.hasLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (session.getLoggedInUser() instanceof ServiceProvider) {
                    controllerActions = new ServiceProviderActions();
                    mainButton.setText("Anfragen");
                } else {
                    controllerActions = new CustomerActions();
                }
            }
            secondButton.setVisible(false);
        });
    }

    /**
     * Goes to the view of the customer, in that he can create an Emergency Advertisement.
     * @param actionEvent to be triggered by the user. Which wants to create an Emergency Advertisement
     */
    @FXML
    public void onCreateEmergencyAd(ActionEvent actionEvent) {
        root.getScene().setRoot(screens.get(Config.ADVERTISEMENT));
    }

    /**
     * Goes to the user profile view of the customer.
     * @param actionEvent to be triggered by the user. Which wants to go to the user profile
     */
    @FXML
    public void onGoToUserProfile(ActionEvent actionEvent) {
        root.getScene().setRoot(screens.get(Config.PROFILE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }

    @FXML
    private void onMainButtonAction(ActionEvent actionEvent) {
        controllerActions.onMainButtonAction(root, screens);
    }
}
