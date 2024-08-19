package ch.zhaw.it.pm3;

/**
 * Utility class used only to store the name and the paths of the views.
 */
public final class Config {

    private Config() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ADVERTISEMENT = "Advertisement";
    public static final String ADVERTISEMENT_FILE = "/views/Advertisement.fxml";
    public static final String LIST_VIEW = "ListView";
    public static final String LIST_VIEW_FILE = "/views/ListView.fxml";
    public static final String HOME = "HomeCustomer";
    public static final String HOME_FILE = "/views/Home.fxml";
    public static final String LOGIN = "Login";
    public static final String LOGIN_FILE = "/views/Login.fxml";
    public static final String OFFER = "Offer";
    public static final String OFFER_FILE = "/views/Offer.fxml";
    public static final String REGISTRATION = "Registration";
    public static final String REGISTRATION_FILE = "/views/Registration.fxml";
    public static final String PROFILE = "ProfileCustomer";
    public static final String PROFILE_FILE = "/views/Profile.fxml";
    public static final String CONTRACT = "Contract";
    public static final String CONTRACT_FILE = "/views/Contract.fxml";
}
