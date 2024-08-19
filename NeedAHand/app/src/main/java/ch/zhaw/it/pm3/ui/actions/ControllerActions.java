package ch.zhaw.it.pm3.ui.actions;

import ch.zhaw.it.pm3.model.Task;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.List;

/**
 * This interface allows the implementation of the strategy pattern in the controllers.
 */
public interface ControllerActions {
    void onGoBack(AnchorPane root, HashMap<String, Parent> screens, boolean fromContractView, boolean fromProfileView);
    void setListView(ListView listView, boolean fromProfileView);
    void onListViewClick(AnchorPane root, ListView listView, HashMap<String, Parent> screens, boolean fromProfileView);
    void switchView(Button contractButton, Label acceptOfferLabel);
    void setLabels(Label listViewTitleLabel, Label customerFirstNameLabel, Label customerNameLabel, Label customerEmailLabel, Label ratingTitleLabel, Label ratingContentLabel);
    List<Task> setListViewOpenTasks();
    void onMainButtonAction(AnchorPane root, HashMap<String, Parent> screens);
}
