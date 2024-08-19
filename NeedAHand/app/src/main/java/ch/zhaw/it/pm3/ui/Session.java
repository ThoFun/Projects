package ch.zhaw.it.pm3.ui;

import ch.zhaw.it.pm3.model.Advertisement;
import ch.zhaw.it.pm3.model.Offer;
import ch.zhaw.it.pm3.model.Task;
import ch.zhaw.it.pm3.model.User;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * this class saves the temporary information of the program that shouldn't be
 * saved after the program is closed. It implements the singleton pattern so
 * that one instance of the class is used for the entire program
 */
public class Session {

    private static Session session;
    private User loggedInUser;

    private Advertisement actualAdvertisement;
    private Offer actualOffer;

    private Task actualTask;

    private BooleanProperty hasLoggedIn = new SimpleBooleanProperty(false);
    private BooleanProperty actualAdvertisementIsSet = new SimpleBooleanProperty(false);
    private BooleanProperty actualOfferIsSet = new SimpleBooleanProperty(false);
    private BooleanProperty actualTaskIsSet = new SimpleBooleanProperty(false);

    private Session() {
    }

    /**
     * This static method return the instance of {@link Session}. it creates an
     * instance of {@link Session} if it still doesn't exist.
     *
     * @return {@link Session} instance.
     */
    public static Session getInstance() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        setHasLoggedIn(true);
    }

    public Advertisement getActualAdvertisement() {
        return actualAdvertisement;
    }

    public void setActualAdvertisement(Advertisement actualAdvertisement) {
        this.actualAdvertisement = actualAdvertisement;
        setActualAdvertisementIsSet(true);
    }

    public Offer getActualOffer() {
        return actualOffer;
    }

    public void setActualOffer(Offer actualOffer) {
        this.actualOffer = actualOffer;
        setActualOfferIsSet(true);
    }

    public BooleanProperty hasLoggedInProperty() {
        return hasLoggedIn;
    }

    public BooleanProperty actualAdvertisementIsSetProperty() {
        return actualAdvertisementIsSet;
    }

    public void setActualAdvertisementIsSet(boolean actualAdvertisementIsSet) {
        this.actualAdvertisementIsSet.set(actualAdvertisementIsSet);
    }

    public BooleanProperty actualOfferIsSetProperty() {
        return actualOfferIsSet;
    }

    public void setActualOfferIsSet(boolean actualOfferIsSet) {
        this.actualOfferIsSet.set(actualOfferIsSet);
    }

    public void setHasLoggedIn(boolean hasLoggedIn) {
        this.hasLoggedIn.set(hasLoggedIn);
    }

    public Task getActualTask() {
        return actualTask;
    }

    public BooleanProperty actualTaskIsSetProperty() {
        return actualTaskIsSet;
    }

    public void setActualTask(Task task){
        this.actualTask = task;
        setActualTaskIsSet(true);
    }

    public void setActualTaskIsSet(boolean actualTaskIsSet) {
        this.actualTaskIsSet.set(actualTaskIsSet);
    }

    public boolean isActualTaskSet() {
        return actualTaskIsSet.get();
    }
}
