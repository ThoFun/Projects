package ch.zhaw.it.pm3.databaseHandling;

import ch.zhaw.it.pm3.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class manages only the data loaded from the {@link Database} (userList).
 * It implements the singleton pattern so that one instance of
 * the class is used for the entire program
 */

public class DataManager {
    private static DataManager dataManager;
    private List<User> userList;
    private ObservableList<Advertisement> advertisementList;
    private ObservableList<Offer> offerList;

    private ObservableList<Task> taskList;

    private DataManager() {
        userList = Database.getUserListFromFile();
        advertisementList = FXCollections.observableArrayList(Database.getAdvertisementListFromFile());
        offerList = FXCollections.observableArrayList(Database.getOfferListFromFile());
        taskList = FXCollections.observableArrayList(Database.getTaskListFromFile());
    }

    /**
     * This static method return the instance of {@link DataManager}. it creates an
     * instance of {@link DataManager} if it still doesn't exist.
     *
     * @return {@link DataManager} instance.
     */
    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    /**
     * This method saves the data to the {@link Database}
     */
    public void saveData() {
        Database.saveListsToFile(userList, advertisementList.stream().collect(Collectors.toList()), offerList.stream().collect(Collectors.toList()), taskList.stream().collect(Collectors.toList()));
    }

    /**
     * This method returns an id that increments from the highest id
     *
     * @param list  given list of objects to get the highest id
     * @param getId method of the object type
     * @param <T>   generic type
     * @return highest id in the list + 1
     */
    public <T> int getNewId(List<T> list, Function<T, Integer> getId) {
        int id = 0;
        List<Integer> ids = list.stream().map(getId).toList();
        if (!ids.isEmpty()) {
            id = Collections.max(ids) + 1;
        }
        return id;
    }

    public List<User> getUserList() {
        return userList;
    }

    /**
     * adds a user to the user list
     *
     * @param user the user to be added
     */
    public void addUser(User user) {
        userList.add(user);
    }

    public ObservableList<Advertisement> getAdvertisementList() {
        return advertisementList;
    }

    /**
     * This method adds an advertisement to the advertisement list
     *
     * @param advertisement the advertisement to be added
     */
    public void addAdvertisementToList(Advertisement advertisement) {
        advertisementList.add(advertisement);
    }

    public ObservableList<Offer> getOfferList() {
        return offerList;
    }

    /**
     * This method adds an offer to the offer list
     *
     * @param offer the offer to be added
     */
    public void addOfferToList(Offer offer) {
        offerList.add(offer);
    }

    public ObservableList<Task> getTaskList() {
        return taskList;
    }

    /**
     * This method adds a task to the task list
     *
     * @param task the task to be added
     */
    public void addTaskToList(Task task) {
        taskList.add(task);
    }

    public boolean hasAdvertisementATask(Advertisement advertisement) {
        for (Task task : taskList) {
            if (task.getAdvertisementId() == advertisement.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns a list of advertisements of the given user
     *
     * @param loggedInUser the user whose advertisements are to be returned
     * @return list of advertisements of the given user
     */
    public List<Advertisement> getAdvertisementsOfUser(User loggedInUser) {
        return advertisementList.stream()
                .filter(advertisement -> advertisement.getCustomerId() == loggedInUser.getId())
                .toList();
    }

    /**
     * This method returns a list of offers of a user
     *
     * @param loggedInUser the logged-in user
     * @return list of offers of the user
     */
    public List<Offer> getOffersOfUser(User loggedInUser) {
        return offerList.stream()
                .filter(offer -> offer.getServiceProviderId() == loggedInUser.getId())
                .toList();
    }

    /**
     * This method returns a list of offers from the given Advertisement
     *
     * @param actualAdvertisement the advertisement to get the offers from
     * @return list of offers
     */
    public List<Offer> getOffersOfAdvertisement(Advertisement actualAdvertisement) {
        return offerList.stream()
                .filter(offer -> offer.getAdvertisementId() == actualAdvertisement.getId())
                .toList();
    }

    /**
     * This method returns a list of tasks from the given User
     *
     * @param loggedInUser the user
     * @return list of tasks
     */
    public List<Task> getTasksOfUser(User loggedInUser) {
        return taskList.stream()
                .filter(task -> getUserById(getAdvertisementById(task.getAdvertisementId()).getCustomerId()).getId() == loggedInUser.getId())
                .toList();
    }

    /**
     * This method returns a list of tasks from the given User
     *
     * @param serviceProvider the user
     * @return list of tasks
     */
    public List<Task> getTasksOfServiceProvider(ServiceProvider serviceProvider) {
        return taskList.stream()
                .filter(task -> getUserById(getOfferById(task.getAdvertisementId()).getServiceProviderId()).getId() == serviceProvider.getId())
                .toList();
    }

    /**
     * This method returns the user with the given id
     *
     * @param id the id of the user
     * @return the user with the given id
     */
    public User getUserById(int id) {
        return userList.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Advertisement getAdvertisementById(int id) {
        return advertisementList.stream()
                .filter(advertisement -> advertisement.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Offer getOfferById(int id) {
        return offerList.stream()
                .filter(offer -> offer.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Advertisement> getAdvertisementsThatDoNotHaveTasks(List<Advertisement> advertisementListWithFinder) {
        if (taskList.size() == 0) {
            return advertisementListWithFinder;
        }
        return advertisementListWithFinder.stream()
                .filter(advertisement -> taskList.stream()
                        .noneMatch(task -> task.getAdvertisementId() == advertisement.getId()))
                .collect(Collectors.toList());
    }

}