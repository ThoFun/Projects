package ch.zhaw.it.pm3.ui;

import javafx.scene.Parent;

import java.util.HashMap;

/**
 * This interface helps to control the different screens.
 */
public interface ControlledScreens {
    /**
     * sets the screen list
     * 
     * @param screens to be set
     */
    public void setScreenList(HashMap<String, Parent> screens);
}
