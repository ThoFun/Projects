package ch.zhaw.pm2.racetrack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

public class ConsoleView implements View {
    private final TextIO textIO;
    private final TextTerminal textTerminal;
    private final Config config;
    private Track track;

    public ConsoleView(TextIO textIO, Config config) {
        this.textIO = textIO;
        textTerminal = textIO.getTextTerminal();
        this.config = config;
        textTerminal.setBookmark("clearAll");
    }

    /**
     * Sets the given track.
     * @param track given track
     */
    public void setTrack(Track track) {
        this.track = track;
    }

    /**
     * Prints the given output value on the terminal.
     * @param outputValue text for terminal
     */
    public void print(String outputValue) {
        textTerminal.print(outputValue);
    }

    /**
     * Loops through the ArrayList of track names and let the user choose one. then return this File.
     *
     * @param folder folder of the track files
     * @return the track file choosen from the user
     */
    @Override
    public File getTrackFile(File folder) {
        List<File> trackList = getTrackFiles(folder);
        List<String> tracksListItems = new ArrayList<>();
        for (int i = 0; i < trackList.size(); i++) {
            tracksListItems.add((i + 1) + ". " + trackList.get(i).getName());
        }
        print("\n" + String.join("\n", tracksListItems));
        print( "\n" + "Please choose your track:");
        int chosenFile = textIO.newIntInputReader().withMinVal(1).withMaxVal(trackList.size()).read();
        return trackList.get(chosenFile-1);
    }

    /**
     * Gets all the filenames from the folder by going through each file, if a 'file' is actually a
     * folder it will apply the method recursively to the folder and extract its files before going
     * back to the original folder.
     *
     * @param folder location that contains the track files
     * @return list with track files (.txt) inside the folder
     */
    private List<File> getTrackFiles(File folder) {
        List<File> tracks = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getTrackFiles(file);
                } else {
                    if (file.toString().endsWith(".txt")) {
                        tracks.add(new File(file.getAbsolutePath()));
                    }
                }
            }
        }
        return tracks;
    }

    /**
     * Returns the associated Direction
     *
     * @param number direction number
     * @return direction
     */
    private PositionVector.Direction numberToDirection(int number){
        return switch (number) {
            case 1 -> PositionVector.Direction.UP_LEFT;
            case 2 -> PositionVector.Direction.UP;
            case 3 -> PositionVector.Direction.UP_RIGHT;
            case 4 -> PositionVector.Direction.LEFT;
            case 6 -> PositionVector.Direction.RIGHT;
            case 7 -> PositionVector.Direction.DOWN_LEFT;
            case 8 -> PositionVector.Direction.DOWN;
            case 9 -> PositionVector.Direction.DOWN_RIGHT;
            default -> PositionVector.Direction.NONE;
        };
    }

    /**
     * Gets the chosen strategy type from the user.
     *
     * @param carName Name of the current car
     * @return chosen strategy
     */
    @Override
    public Config.StrategyType getMoveStrategy(String carName) {
        List<String> strategyNames = new ArrayList<>();
        List<Config.StrategyType> moveStrategies = config.getImplementedMoveStrategies();

        for(Config.StrategyType strategyType : moveStrategies) {
            strategyNames.add(strategyType.toString());
        }

        String prompt = "\nNow choose a move strategy for car " +  carName + ":";
        String choosenStrategy = textIO.newStringInputReader().withNumberedPossibleValues(strategyNames).read(prompt);

        try {
            return Config.StrategyType.valueOf(choosenStrategy);
        } catch (Exception e) {
            return getMoveStrategy(carName);
        }
    }

    /**
     * Handles the User Input in the nextMove step.
     *
     * @return direction choosen from the user
     */
    @Override
    public PositionVector.Direction nextUserMover() {
        String prompt = "Acceleration direction (h for help) (1, 2, 3, 4, 5, 6, 7, 8, 9)";
        char input = textIO.newCharInputReader().read(prompt);

        try {
            return numberToDirection(Integer.parseInt(String.valueOf(input)));
        } catch (NumberFormatException e) {
            if(!handleCharInput(input)) {
                print("invalid command, try again!\n");
            }
        } catch (IllegalArgumentException e) {
            print("invalid Number, try again!\n");
        }

        return nextUserMover();
    }

    /**
     * Prints the help instructions.
     */
    private void printHelp() {
        print("Acceleration Directions: \n" +
                "1  2  3    1 = UP-LEFT     2 = UP      3 = UP-RIGHT\n" +
                "4  5  6    4 = LEFT        5 = NONE    6 = RIGHT\n" +
                "7  8  9    7 = DOWN-LEFT   8 = DOWN    9 = DOWN-RIGHT\n");
        print("\nh for help");
        print("\nt to show track");
        print("\nq to quit game\n");
    }

    /**
     * Helps the user with displaying help, the track or quiting the game.
     *
     * @param input input of the user
     * @return false, if input is wrong, otherwise true.
     */
    private boolean handleCharInput(char input) {
        switch (input) {
            case 'h' -> printHelp();
            case 't' -> printTrack();
            case 'q' -> System.exit(0);
            default -> {
                print("invalid input, try again:\n");
                return false;
            }
        }
        return true;
    }

    /**
     * Prints the track onto the terminal
     */
    public void printTrack() {
        if(null != track) {
            print(track.toString());
        } else {
            print("No track defined in view");
        }
    }

    /**
     * Displays NO WINNER onto the terminal
     */
    public void printNoWinner() {
        print("NO WINNER\n");
    }

    /**
     * Displays the winner onto the terminal.
     * @param winner the char tag from the car
     */
    public void printWinner(char winner) {
        print("AND THE WINNER IS...\nCAR " + winner + "\n");
    }

    /**
     * Displays a welcome text.
     */
    public void printWelcomeText(){
        textTerminal.print(
            "  _____             _______             _    \n"
                + " |  __ \\           |__   __|           | |   \n"
                + " | |__) |__ _  ___ ___| |_ __ __ _  ___| | __\n"
                + " |  _  // _` |/ __/ _ \\ | '__/ _` |/ __| |/ /\n"
                + " | | \\ \\ (_| | (_|  __/ | | | (_| | (__|   < \n"
                + " |_|  \\_\\__,_|\\___\\___|_|_|  \\__,_|\\___|_|\\_\\\n"
                + "                                             \n"
                + "                                             \n"
                + "Welcome to RaceTrack! Please choose one of the following tracks:\n");

    }

    /**
     * Resets the console to empty.
     */
    public void clearTextTerminal() {
        textTerminal.resetToBookmark("clearAll");
    }

    /**
     * Waits for a input "q" and then system.exit(0)
     */
    public void waitForExit() {
        String prompt = "\n\nPress q to quit: ";
        char input = textIO.newCharInputReader().read(prompt);
        if ('q' == input) {
            System.exit(0);
        } else {
            waitForExit();
        }
    }
}
