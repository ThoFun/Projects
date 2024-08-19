package ch.zhaw.it.pm3.databaseHandling;

import ch.zhaw.it.pm3.model.Advertisement;
import ch.zhaw.it.pm3.model.Offer;
import ch.zhaw.it.pm3.model.Task;
import ch.zhaw.it.pm3.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This abstract class works as a database to save and load the list of the
 * users in a file in {@link Database#DATA_FILE_PATH}
 */

public abstract class Database {
    private static final String DATA_FILE_PATH = "database" + File.separator + "data.dat";
    private static final Logger logger = Logger.getLogger("DatabaseLogger");

    private Database() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * gets the user list from the {@link Database#DATA_FILE_PATH}.
     *
     * @return the user list loaded from the file, or an empty list if file doesn't exist
     */
    public static List<User> getUserListFromFile() {
        List<User> userListFromFile = new ArrayList<>();
        try (ObjectInputStream objInStream = new ObjectInputStream(new FileInputStream(DATA_FILE_PATH))) {
            userListFromFile = (ArrayList<User>) objInStream.readObject();
        } catch (FileNotFoundException e) {
            logger.log(Level.INFO, "File not found! " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "class not found: " + e.getMessage());
        }
        return userListFromFile;
    }

    /**
     * gets the advertisement list from the {@link Database#DATA_FILE_PATH}.
     *
     * @return the advertisement list loaded from the file, or an empty list if file doesn't exist
     */
    public static List<Advertisement> getAdvertisementListFromFile() {
        List<Advertisement> advertisementListFromFile = new ArrayList<>();
        try (ObjectInputStream objInStream = new ObjectInputStream(new FileInputStream(DATA_FILE_PATH))) {
            objInStream.readObject(); // to skip the user list
            advertisementListFromFile = (ArrayList<Advertisement>) objInStream.readObject();
        } catch (FileNotFoundException e) {
            logger.log(Level.INFO, "File not found! " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "class not found: " + e.getMessage());
        }
        return advertisementListFromFile;
    }

    /**
     * gets the offer list from the {@link Database#DATA_FILE_PATH}.
     *
     * @return the offer list loaded from the file, or an empty list if file doesn't exist
     */
    public static List<Offer> getOfferListFromFile() {
        List<Offer> offerListFromFile = new ArrayList<>();
        try (ObjectInputStream objInStream = new ObjectInputStream(new FileInputStream(DATA_FILE_PATH))) {
            objInStream.readObject(); // to skip the user list
            objInStream.readObject(); // to skip the advertisement list
            offerListFromFile = (ArrayList<Offer>) objInStream.readObject();
        } catch (FileNotFoundException e) {
            logger.log(Level.INFO, "File not found! " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "class not found: " + e.getMessage());
        }
        return offerListFromFile;
    }

    /**
     * gets the task list from the {@link Database#DATA_FILE_PATH}.
     *
     * @return the task list loaded from the file, or an empty list if file doesn't exist
     */
    public static List<Task> getTaskListFromFile() {
        List<Task> taskListFromFile = new ArrayList<>();
        try (ObjectInputStream objInStream = new ObjectInputStream(new FileInputStream(DATA_FILE_PATH))) {
            objInStream.readObject(); // to skip the user list
            objInStream.readObject(); // to skip the advertisement list
            objInStream.readObject(); // to skip the offer list
            taskListFromFile = (ArrayList<Task>) objInStream.readObject();
        } catch (FileNotFoundException e) {
            logger.log(Level.INFO, "File not found! " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "class not found: " + e.getMessage());
        }
        return taskListFromFile;
    }


    /**
     * saves the user list to the {@link Database#DATA_FILE_PATH}.
     *
     * @param userList the user list to be saved
     * @param advertisementList the advertisement list to be saved
     * @param offerList the offer list to be saved
     */
    public static void saveListsToFile(List<User> userList, List<Advertisement> advertisementList, List<Offer> offerList, List<Task> taskList) {
        File dataFile = new File(DATA_FILE_PATH);
        dataFile.getParentFile().mkdirs();
        try (ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            objOutStream.writeObject(userList);
            objOutStream.writeObject(advertisementList);
            objOutStream.writeObject(offerList);
            objOutStream.writeObject(taskList);
        } catch (FileNotFoundException e) {
            logger.log(Level.INFO, "File not found! " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error: " + e.getMessage());
        }
    }
}
