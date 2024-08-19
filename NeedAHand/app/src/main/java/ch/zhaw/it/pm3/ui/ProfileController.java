package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.databaseHandling.DataManager;
import ch.zhaw.it.pm3.model.Advertisement;
import ch.zhaw.it.pm3.model.Offer;
import ch.zhaw.it.pm3.model.ServiceProvider;
import ch.zhaw.it.pm3.model.Task;
import ch.zhaw.it.pm3.ui.actions.ControllerActions;
import ch.zhaw.it.pm3.ui.actions.CustomerActions;
import ch.zhaw.it.pm3.ui.actions.ServiceProviderActions;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.List;

public class ProfileController implements ControlledScreens {
    private HashMap<String, Parent> screens = new HashMap<>();
    private DataManager dataManager = DataManager.getInstance();
    private Session session = Session.getInstance();
    private ControllerActions controllerActions;

    @FXML
    private Label listViewTitleLabel;
    @FXML
    private ListView listView;

    @FXML
    private AnchorPane root;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label customerFirstNameLabel;

    @FXML
    private Label customerEmailLabel;

    @FXML
    private Label ratingContentLabel;

    @FXML
    private Label ratingTitleLabel;

    @FXML
    private ListView<Task> openTasksListView;

    @FXML
    void initialize() {
        session.hasLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (session.getLoggedInUser() instanceof ServiceProvider) {
                    controllerActions = new ServiceProviderActions();
                } else {
                    controllerActions = new CustomerActions();
                }
                setLabels();
                setListView();
                setListViewOpenTasks();
            }
        });
        taskObserver();
        dataManager.getAdvertisementList().addListener((ListChangeListener<? super Advertisement>) observable -> setListView());
        dataManager.getOfferList().addListener((ListChangeListener<? super Offer>) observable -> setListView());
        dataManager.getTaskList().addListener((ListChangeListener<? super Task>) observable -> setListViewOpenTasks());
    }

    private void setLabels() {
        controllerActions.setLabels(listViewTitleLabel, customerFirstNameLabel, customerNameLabel, customerEmailLabel, ratingTitleLabel, ratingContentLabel);
    }

    private void setListView() {
        controllerActions.setListView(listView, true);
    }

    private void setListViewOpenTasks() {
        List<Task> openTasks = controllerActions.setListViewOpenTasks();
        if (openTasks != null) {
            openTasksListView.setItems(FXCollections.observableArrayList(openTasks));
        }
    }

    private void taskObserver() {
        session.actualTaskIsSetProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setListView();
                setListViewOpenTasks();
            }
        });
    }

    @FXML
    void onGoBack(ActionEvent event) {
        controllerActions.onGoBack(root, screens, false, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScreenList(HashMap<String, Parent> screens) {
        this.screens = screens;
    }

    /**
     * This method forwards a user to the view ListView of the selected advertisement.
     *
     * @param mouseEvent The mouse event.
     */
    public void onListViewClick(MouseEvent mouseEvent) {
        controllerActions.onListViewClick(root, listView, screens, true);
    }

    /**
     * This method forwards a user to the view ContractCustomer of the selected task.
     *
     * @param mouseEvent The mouse event.
     */
    public void onListViewClickTask(MouseEvent mouseEvent) {
        Task clickedTask = openTasksListView.getSelectionModel().getSelectedItem();
        if (clickedTask != null) {
            session.setActualTask(clickedTask);
            root.getScene().setRoot(screens.get(Config.CONTRACT));
        }
    }
}