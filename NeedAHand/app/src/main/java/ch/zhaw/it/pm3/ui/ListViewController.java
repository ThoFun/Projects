package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.model.ServiceProvider;
import ch.zhaw.it.pm3.ui.actions.ControllerActions;
import ch.zhaw.it.pm3.ui.actions.CustomerActions;
import ch.zhaw.it.pm3.ui.actions.ServiceProviderActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;

/**
 * This class is the controller for the Lists of the Advertisements. It creates an Update of the ServiceProvider.
 * It is responsible to show the Advertisement in a list.
 */
public class ListViewController implements ControlledScreens {
    private HashMap<String, Parent> screens = new HashMap<>();
    private Session session = Session.getInstance();
    private ControllerActions controllerActions;

    @FXML
    private AnchorPane root;
    @FXML
    private ListView listView;
    @FXML
    private Label titleLabel;

    @FXML
    void initialize() {
        session.hasLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (session.getLoggedInUser() instanceof ServiceProvider) {
                    controllerActions = new ServiceProviderActions();
                    titleLabel.setText("Inserate");
                } else {
                    controllerActions = new CustomerActions();
                }
                setListView();
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

    @FXML
    private void onGoBack(ActionEvent actionEvent) {
        controllerActions.onGoBack(root, screens, false, false);
        session.setActualAdvertisementIsSet(false);
    }

    private void setListView() {
        controllerActions.setListView(listView, false);
    }

    /**
     * This method forwards a user to the views Advertisement and Contract_Customer, depending on if the user is logged in as a ServiceProvider or a Customer.
     *
     * @param mouseEvent The click of the mouse.
     */
    public void onListViewClick(MouseEvent mouseEvent) {
        controllerActions.onListViewClick(root, listView, screens, false);
    }
}
