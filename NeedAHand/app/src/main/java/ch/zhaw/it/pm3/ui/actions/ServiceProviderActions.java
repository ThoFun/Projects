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

public class ServiceProviderActions implements ControllerActions {
    private DataManager dataManager;
    private Session session;
    private ServiceProvider serviceProvider;

    public ServiceProviderActions() {
        dataManager = DataManager.getInstance();
        session = Session.getInstance();
        serviceProvider = (ServiceProvider) session.getLoggedInUser();
    }

    @Override
    public void onGoBack(AnchorPane root, HashMap<String, Parent> screens, boolean fromContractView, boolean fromProfileView) {
        if (fromContractView) {
            root.getScene().setRoot(screens.get(Config.PROFILE));
        } else {
            session.setActualOfferIsSet(false);
            root.getScene().setRoot(screens.get(Config.HOME));
        }
    }

    @Override
    public void setListView(ListView listView, boolean fromProfileView) {
        if (fromProfileView) {
            listView.setItems(FXCollections.observableArrayList(dataManager.getOffersOfUser(serviceProvider)));
        } else {
            List<Advertisement> advertisementList = dataManager.getAdvertisementList();
            List<Advertisement> advertisementListWithFinder = Finder.findAdvertisements(advertisementList, (ServiceProvider) session.getLoggedInUser());
            advertisementListWithFinder = dataManager.getAdvertisementsThatDoNotHaveTasks(advertisementListWithFinder);
            listView.setItems(FXCollections.observableArrayList(advertisementListWithFinder));
        }
    }

    @Override
    public void onListViewClick(AnchorPane root, ListView listView, HashMap<String, Parent> screens, boolean fromProfile) {
        if (fromProfile) {
            Offer offer = (Offer) listView.getSelectionModel().getSelectedItem();
            if (offer != null) {
                session.setActualOffer(offer);
                root.getScene().setRoot(screens.get(Config.CONTRACT));
            }
        } else {
            Advertisement clickedAdvertisement = (Advertisement) listView.getSelectionModel().getSelectedItem();
            if (clickedAdvertisement != null) {
                session.setActualAdvertisement(clickedAdvertisement);
                root.getScene().setRoot(screens.get(Config.ADVERTISEMENT));
            }
        }
    }

    @Override
    public void switchView(Button contractButton, Label acceptOfferLabel) {
        contractButton.setVisible(false);
        acceptOfferLabel.setVisible(false);
    }

    @Override
    public void setLabels(Label listViewTitleLabel, Label customerFirstNameLabel, Label customerNameLabel, Label customerEmailLabel, Label ratingTitleLabel, Label ratingContentLabel) {
        listViewTitleLabel.setText("Erstellte Offerten:");
        customerFirstNameLabel.setText(serviceProvider.getFirstName());
        customerNameLabel.setText(serviceProvider.getSurname());
        customerEmailLabel.setText(serviceProvider.getMailAddress());
        ratingContentLabel.setText(String.valueOf(serviceProvider.getRating()));
    }

    @Override
    public List<Task> setListViewOpenTasks() {
        return dataManager.getTasksOfServiceProvider(serviceProvider);
    }

    public void onMainButtonAction(AnchorPane root, HashMap<String, Parent> screens) {
        root.getScene().setRoot(screens.get(Config.LIST_VIEW));
    }
}
