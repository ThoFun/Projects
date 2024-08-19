package ch.zhaw.pm2.checkit.client;

import javafx.scene.Parent;

import java.util.HashMap;

/**
 * Interface with one method to set the list of actual available
 * screens with a key (name) of the screen
 */
public interface ControlledScreens {
    /**
     * Sets the list with the available sceens
     * @param screens available screens
     */
    void setScreenList(HashMap<String, Parent> screens);
}
