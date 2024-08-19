package ch.zhaw.it.pm3.ui.actions;

import ch.zhaw.it.pm3.Config;
import ch.zhaw.it.pm3.databaseHandling.DataManager;
import ch.zhaw.it.pm3.model.*;
import ch.zhaw.it.pm3.ui.Session;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.List;

public class CustomerActions implements ControllerActions {
    private DataManager dataManager;
    private Session session;
    private Customer customer;

    public CustomerActions() {
        dataManager = DataManager.getInstance();
        session = Session.getInstance();
        customer = (Customer) session.getLoggedInUser();
    }

    @Override
    public void onGoBack(AnchorPane root, HashMap<String, Parent> screens, boolean fromContractView, boolean fromProfileView) {
        if (fromProfileView) {
            root.getScene().setRoot(screens.get(Config.HOME));
        } else {
            root.getScene().setRoot(screens.get(Config.PROFILE));
        }
    }

    @Override
    public void setListView(ListView listView, boolean fromProfileView) {
        if (fromProfileView) {
            listView.setItems(FXCollections.observableArrayList(dataManager.getAdvertisementsOfUser(customer)));
        } else {
            session.actualAdvertisementIsSetProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    Advertisement actualAdvertisement = session.getActualAdvertisement();
                    listView.setItems(FXCollections.observableArrayList(dataManager.getOffersOfAdvertisement(actualAdvertisement)));
                }
            });
        }
    }

    @Override
    public void onListViewClick(AnchorPane root, ListView listView, HashMap<String, Parent> screens, boolean fromProfile) {
        if (fromProfile) {
            Advertisement clickedAdvertisement = (Advertisement) listView.getSelectionModel().getSelectedItem();
            if (clickedAdvertisement != null && !dataManager.hasAdvertisementATask(clickedAdvertisement)) {
                session.setActualAdvertisement(clickedAdvertisement);
                root.getScene().setRoot(screens.get(Config.LIST_VIEW));
            }
        } else {
            Offer clickedOffer = (Offer) listView.getSelectionModel().getSelectedItem();
            if (clickedOffer != null) {
                session.setActualOffer(clickedOffer);
                root.getScene().setRoot(screens.get(Config.CONTRACT));
            }
        }
    }

    @Override
    public void switchView(Button contractButton, Label acceptOfferLabel) {
        session.actualTaskIsSetProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && session.getActualTask().getStatus() == TaskStatus.IN_PROGRESS) {
                contractButton.setVisible(true);
                acceptOfferLabel.setVisible(true);
                contractButton.setText("Auftrag abschliessen");
                acceptOfferLabel.setText("Moechten Sie den Auftrag abschliessen?");
            } else if (newValue && session.isActualTaskSet() && session.getActualTask().getStatus() == TaskStatus.DONE) {
                contractButton.setVisible(false);
                acceptOfferLabel.setText("Dieser Auftrag ist abgeschlossen");
            } else {
                contractButton.setVisible(true);
                contractButton.setText("Auftrag annehmen");
                acceptOfferLabel.setText("Moechten Sie den Auftrag annehmen?");
            }
        });
    }

    @Override
    public void setLabels(Label listViewTitleLabel, Label customerFirstNameLabel, Label customerNameLabel, Label customerEmailLabel, Label ratingTitleLabel, Label ratingContentLabel) {
        customerFirstNameLabel.setText(customer.getFirstName());
        customerNameLabel.setText(customer.getSurname());
        customerEmailLabel.setText(customer.getMailAddress());
        ratingTitleLabel.setVisible(false);
    }

    @Override
    public List<Task> setListViewOpenTasks() {
        return dataManager.getTasksOfUser(customer);
    }

    public void onMainButtonAction(AnchorPane root, HashMap<String, Parent> screens) {
        root.getScene().setRoot(screens.get(Config.ADVERTISEMENT));
    }
}
