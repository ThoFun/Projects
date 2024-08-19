package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.given.TrackSpecification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class represents the racetrack board.
 *
 * <p>The racetrack board consists of a rectangular grid of 'width' columns and 'height' rows.
 * The zero point of he grid is at the top left. The x-axis points to the right and the y-axis points downwards.</p>
 * <p>Positions on the track grid are specified using {@link PositionVector} objects. These are vectors containing an
 * x/y coordinate pair, pointing from the zero-point (top-left) to the addressed space in the grid.</p>
 *
 * <p>Each position in the grid represents a space which can hold an enum object of type {@link Config.SpaceType}.<br>
 * Possible Space types are:
 * <ul>
 *  <li>WALL : road boundary or off track space</li>
 *  <li>TRACK: road or open track space</li>
 *  <li>FINISH_LEFT, FINISH_RIGHT, FINISH_UP, FINISH_DOWN :  finish line spaces which have to be crossed
 *      in the indicated direction to winn the race.</li>
 * </ul>
 * <p>Beside the board the track contains the list of cars, with their current state (position, velocity, crashed,...)</p>
 *
 * <p>At initialization the track grid data is read from the given track file. The track data must be a
 * rectangular block of text. Empty lines at the start are ignored. Processing stops at the first empty line
 * following a non-empty line, or at the end of the file.</p>
 * <p>Characters in the line represent SpaceTypes. The mapping of the Characters is as follows:</p>
 * <ul>
 *   <li>WALL : '#'</li>
 *   <li>TRACK: ' '</li>
 *   <li>FINISH_LEFT : '&lt;'</li>
 *   <li>FINISH_RIGHT: '&gt;'</li>
 *   <li>FINISH_UP   : '^;'</li>
 *   <li>FINISH_DOWN: 'v'</li>
 *   <li>Any other character indicates the starting position of a car.<br>
 *       The character acts as the id for the car and must be unique.<br>
 *       There are 1 to {@link Config#MAX_CARS} allowed. </li>
 * </ul>
 *
 * <p>All lines must have the same length, used to initialize the grid width).
 * Beginning empty lines are skipped.
 * The the tracks ends with the first empty line or the file end.<br>
 * An {@link InvalidTrackFormatException} is thrown, if
 * <ul>
 *   <li>not all track lines have the same length</li>
 *   <li>the file contains no track lines (grid height is 0)</li>
 *   <li>the file contains more than {@link Config#MAX_CARS} cars</li>
 * </ul>
 *
 * <p>The Track can return a String representing the current state of the race (including car positons)</p>
 */
public class Track implements TrackSpecification {
    public static final char CRASH_INDICATOR = 'X';
    private final List<Car> cars = new ArrayList<>();
    private Config.SpaceType[][] board;

    /**
     * Initialize a Track from the given track file.
     *
     * @param trackFile Reference to a file containing the track data
     * @throws FileNotFoundException       if the given track file could not be found
     * @throws InvalidTrackFormatException if the track file contains invalid data (no tracklines, ...)
     */
    public Track(File trackFile) throws FileNotFoundException, InvalidTrackFormatException {
        List<String> rows = new ArrayList<>();

        Scanner scanner = new Scanner(new FileInputStream(trackFile), "UTF-8");
        while (scanner.hasNextLine()) {
            rows.add(scanner.nextLine());
        }
        board = initializeBoard(rows);
    }

    /**
     * Validates the board rows and initializes the board
     * The car's will be saved in the cars-list
     *
     * @param boardRows amount of rows for the board
     * @return a cleanboard with cars
     * @throws InvalidTrackFormatException if the track format is invalid
     */
    private Config.SpaceType[][] initializeBoard(List<String> boardRows) throws InvalidTrackFormatException {
        char[][] dirtyBoard = boardRowsToBoardFormat(boardRows);
        Config.SpaceType[][] cleanBoard = new Config.SpaceType[dirtyBoard.length][dirtyBoard[0].length];
        boolean hasFinish = false;

        for (int xIndex = 0; xIndex < dirtyBoard.length; xIndex++) {
            for (int yIndex = 0; yIndex < dirtyBoard[xIndex].length; yIndex++) {
                //Now we can loop over all items ;)
                char field = dirtyBoard[xIndex][yIndex];
                try {
                    Config.SpaceType spaceType = Config.SpaceType.fromChar(field);
                    cleanBoard[xIndex][yIndex] = spaceType;
                    if(isFinishSign(spaceType)) hasFinish = true;
                } catch (IllegalArgumentException e) {
                    cars.add(initializeNewCar(field, xIndex, yIndex));
                    cleanBoard[xIndex][yIndex] = Config.SpaceType.TRACK;
                }
            }
        }

        if(!hasFinish || cars.size() > Config.MAX_CARS || cars.isEmpty()) {
            throw new InvalidTrackFormatException("Invalid track content");
        }

        return cleanBoard;
    }

    /**
     * Initializes a new car and check if the given id doesn't already exist
     *
     * @param id the new (unique) id for the car
     * @param xIndex position of the car
     * @param yIndex position of the car
     * @return new car
     * @throws InvalidTrackFormatException if the car id already exists
     */
    private Car initializeNewCar(char id, int xIndex, int yIndex) throws InvalidTrackFormatException {
        for (Car car : cars) {
            if(car.getId() == id) throw new InvalidTrackFormatException("Several same cars");
        }
        PositionVector position = new PositionVector(xIndex, yIndex);
        return new Car(id, position);
    }

    /**
     * Helps with identifying, if the given spaceType is part of a finish-line
     *
     * @param spaceType Required spacetype of the finish-line
     * @return true, when there is a finish-lin, otherwise false
     */
    private boolean isFinishSign(Config.SpaceType spaceType) {
        return switch (spaceType) {
            case FINISH_DOWN, FINISH_UP, FINISH_LEFT, FINISH_RIGHT -> true;
            default -> false;
        };
    }

    /**
     * Checks the board format and returns a new board with chars in it
     *
     * @param boardRows list of board rows
     * @return track-valid 2-Dimension Array filled with char's
     * @throws InvalidTrackFormatException if the track format is invalid
     */
    private char[][] boardRowsToBoardFormat(List<String> boardRows) throws InvalidTrackFormatException {
        char[][] charBoard = new char[boardRows.get(0).length()][boardRows.size()];

        if(!trackSizeIsValid(boardRows)) throw new InvalidTrackFormatException("Invalid track size");

        for(int yIndex = 0; yIndex < boardRows.size(); yIndex++) {
            char[] characters = boardRows.get(yIndex).toCharArray();

            for(int xIndex = 0; xIndex < characters.length; xIndex++) {
                charBoard[xIndex][yIndex] = characters[xIndex];
            }
        }

        return charBoard;
    }

    /**
     * Return the type of space at the given position.
     * If the location is outside the track bounds, it is considered a wall.
     *
     * @param position The coordinates of the position to examine
     * @return The type of track position at the given location
     */
    @Override
    public Config.SpaceType getSpaceType(PositionVector position) {
        try {
            return board[position.getX()][position.getY()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return Config.SpaceType.WALL;
        }
    }

    /**
     * Return the number of cars.
     * @return Number of cars
     */
    @Override
    public int getCarCount() {
        return cars.size();
    }

    /**
     * Get instance of specified car.
     * @param carIndex The zero-based carIndex number
     * @return The car instance at the given index
     */
    @Override
    public Car getCar(int carIndex) {
        return cars.get(carIndex);
    }

    /**
     * Get the id of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return A char containing the id of the car
     */
    @Override
    public char getCarId(int carIndex) {
        return getCar(carIndex).getId();
    }

    /**
     * Get the position of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return A PositionVector containing the car's current position
     */
    @Override
    public PositionVector getCarPos(int carIndex) {
        return getCar(carIndex).getPosition();
    }

    /**
     * Get the velocity of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return A PositionVector containing the car's current velocity
     */
    @Override
    public PositionVector getCarVelocity(int carIndex) {
        return getCar(carIndex).getVelocity();
    }

    /**
     * Gets character at the given position.
     * If there is a crashed car at the position, {@link #CRASH_INDICATOR} is returned.
     * If there are more than one car at the position, it returns the one which isn't crashed if possible.
     *
     * @param y            position Y-value
     * @param x            position X-vlaue
     * @param currentSpace char to return if no car is at position (x,y)
     * @return character representing position (x,y) on the track
     */
    @Override
    public char getCharAtPosition(int y, int x, Config.SpaceType currentSpace) {
        char returnValue;
        List<Car> carsOnField = getCars(x, y);

        if(carsOnField.size() == 1) {
            return carsOnField.get(0).isCrashed() ? CRASH_INDICATOR : carsOnField.get(0).getId();
        } else if (carsOnField.size() > 1) {
            returnValue = carsOnField.get(0).toString().toCharArray()[0];
            for(Car car : carsOnField) {
                if(!car.isCrashed()) {
                    returnValue = car.toString().toCharArray()[0];
                }
            }
        } else {
            returnValue = currentSpace.getValue();
        }

        return returnValue;
    }

    /**
     * Return a String representation of the track, including the car locations.
     * @return A String representation of the track
     */
    @Override
    public String toString() {
        StringBuilder trackText = new StringBuilder();

        for (int yIndex = 0; yIndex < board[0].length; yIndex++) {
            for (int xIndex = 0; xIndex < board.length; xIndex++) {
                trackText.append(getCharAtPosition(yIndex, xIndex, board[xIndex][yIndex]));
            }
            trackText.append('\n');
        }

        return trackText.toString();
    }

    /**
     * Gets a car
     * @param xIndex x position of car
     * @param yIndex y position of car
     * @return a car
     */
    private List<Car> getCars(int xIndex, int yIndex) {
        List<Car> carsOnPosition = new ArrayList<>();
        PositionVector position = new PositionVector(xIndex, yIndex);
        for (Car car : cars) {
            if(car.getPosition().equals(position)) carsOnPosition.add(car);
        }
        return carsOnPosition;
    }

    /**
     * Gets list of all cars.
     * @return list of cars
     */
    public List<Car> getCars() {
        return cars;
    }

    /**
     * Checks if the size of the track is correct.
     * @param rows list of all the rows
     * @return true, when size is correct, otherwise false
     */
    private boolean trackSizeIsValid(List<String> rows) {
        int rowLength = rows.get(0).length();

        for(String row : rows) {
            if(rowLength != row.length()) return false;
        }

        return true;
    }
}
