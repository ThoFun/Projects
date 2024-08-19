package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.given.GameSpecification;

import java.util.ArrayList;
import java.util.List;

import static ch.zhaw.pm2.racetrack.PositionVector.Direction;

/**
 * Game controller class, performing all actions to modify the game state.
 * It contains the logic to move the cars, detect if they are crashed
 * and if we have a winner.
 */
public class Game implements GameSpecification {
    public static final int NO_WINNER = -1;
    private final Track track;
    private int currentCarIndex = 0;
    private int winner = NO_WINNER;
    private List<Car> cars;

    public Game(Track track) {
        this.track = track;
        cars = track.getCars();
    }

    /**
     * Get the active cars.
     * @return ID of the active cars
     */
    public Car getActiveCar() {
        return cars.get(getCurrentCarIndex());
    }

    /**
     * Return the index of the current active car.
     * Car indexes are zero-based, so the first car is 0, and the last car is getCarCount() - 1.
     * @return The zero-based number of the current car
     */
    @Override
    public int getCurrentCarIndex() {
        return currentCarIndex;
    }

    /**
     * Get the id of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return A char containing the id of the car
     */
    @Override
    public char getCarId(int carIndex) {
        return cars.get(carIndex).getId();
    }

    /**
     * Returns the track.
     * @return the track
     */
    public Track getTrack() {
        return track;
    }

    /**
     * Get the position of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return A PositionVector containing the car's current position
     */
    @Override
    public PositionVector getCarPosition(int carIndex) {
        return cars.get(carIndex).getPosition();
    }

    /**
     * Get the velocity of the specified car.
     * @param carIndex The zero-based carIndex number
     * @return A PositionVector containing the car's current velocity
     */
    @Override
    public PositionVector getCarVelocity(int carIndex) {
        return cars.get(carIndex).getVelocity();
    }

    /**
     * Return the winner of the game. If the game is still in progress, returns NO_WINNER.
     * @return The winning car's index (zero-based, see getCurrentCar()), or NO_WINNER if the game is still in progress
     */
    @Override
    public int getWinner() {
        List<Integer> carsInGame = new ArrayList<>();

        for(int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);

            if(!car.isCrashed()) carsInGame.add(i);
            if(car.isWinner()) {
                winner = i;
            }
        }

        if(carsInGame.size()==1) {
            winner = carsInGame.get(0);
            cars.get(carsInGame.get(0)).setWinner();
        }

        return winner;
    }

    /**
     * Execute the next turn for the current active car.
     * <p>This method changes the current car's velocity and checks on the path to the next position,
     * if it crashes (car state to crashed) or passes the finish line in the right direction (set winner state).</p>
     * <p>The steps are as follows</p>
     * <ol>
     *   <li>Accelerate the current car</li>
     *   <li>Calculate the path from current (start) to next (end) position
     *       (see {@link Game#calculatePath(PositionVector, PositionVector)})</li>
     *   <li>Verify for each step what space type it hits:
     *      <ul>
     *          <li>TRACK: check for collision with other car (crashed &amp; don't continue), otherwise do nothing</li>
     *          <li>WALL: car did collide with the wall - crashed &amp; don't continue</li>
     *          <li>FINISH_*: car hits the finish line - wins only if it crosses the line in the correct direction</li>
     *      </ul>
     *   </li>
     *   <li>If the car crashed or wins, set its position to the crash/win coordinates</li>
     *   <li>If the car crashed, also detect if there is only one car remaining, remaining car is the winner</li>
     *   <li>Otherwise move the car to the end position</li>
     * </ol>
     * <p>The calling method must check the winner state and decide how to go on. If the winner is different
     * than {@link Game#NO_WINNER}, or the current car is already marked as crashed the method returns immediately.</p>
     *
     * @param acceleration A Direction containing the current cars acceleration vector (-1,0,1) in x and y direction
     *                     for this turn
     */
    @Override
    public void doCarTurn(Direction acceleration) {
        Car car = getActiveCar();
        car.accelerate(acceleration);

        PositionVector startPosition = getCarPosition(currentCarIndex);
        PositionVector nextPosition = car.nextPosition();

        List<PositionVector> carPath = calculatePath(startPosition, nextPosition);
        PositionVector tempLastPosition = startPosition;

        for (PositionVector position : carPath){
            try {
                if (!car.isCrashed() && willCarCrash(currentCarIndex, position) && position != carPath.get(0)){
                    car.crash();
                    car.setPosition(position);
                } else if (carWins(car, tempLastPosition, position)) {
                    car.setWinner();
                    car.setPosition(position);
                    return;
                }

            } catch (ArrayIndexOutOfBoundsException e) { //catches fields outside of the track
                car.crash();
                car.setPosition(tempLastPosition);
            }
            tempLastPosition = position;
        }

        if(!car.isCrashed()) {
            car.move();
        }
    }

    /**
     * Checks if the car wins and set it to winner. If the finish line is crossed in the wrong direction, it crashes the car.
     *
     * @param lastPosition the last position, where the car was
     * @param currentPosition the current position, (this needs to be 1 field away from lastPosition)
     * @return rather the car wins or not
     */
    private boolean carWins(Car car, PositionVector lastPosition, PositionVector currentPosition) {
        int yDirection = currentPosition.getY() - lastPosition.getY();
        int xDirection = currentPosition.getX() - lastPosition.getX();

        Config.SpaceType currentSpaceType = track.getSpaceType(currentPosition);
        switch (currentSpaceType) {
            case FINISH_DOWN -> {
                if(yDirection > 0) {
                    car.setWinner();
                } else {
                    car.crash();
                }
            }
            case FINISH_LEFT -> {
                if(xDirection < 0) {
                    car.setWinner();
                } else {
                    car.crash();
                }
            }
            case FINISH_RIGHT -> {
                if(xDirection > 0) {
                    car.setWinner();
                } else {
                    car.crash();
                }
            }
            case FINISH_UP -> {
                if(yDirection < 0) {
                    car.setWinner();
                } else {
                    car.crash();
                }
            }
        }
        return car.isWinner();
    }

    /**
     * Switches to the next car who is still in the game. Skips crashed cars.
     */
    @Override
    public void switchToNextActiveCar() {
        if(!activeCarInGame()) return;

        if (currentCarIndex < cars.size() - 1) {
            currentCarIndex++;
        } else {
            currentCarIndex = 0;
        }

        if(activeCarInGame() && getActiveCar().isCrashed()){
            switchToNextActiveCar();
        }
    }

    /**
     * Returns all of the grid positions in the path between two positions, for use in determining line of sight.
     * Determine the 'pixels/positions' on a raster/grid using Bresenham's line algorithm.
     * (https://de.wikipedia.org/wiki/Bresenham-Algorithmus)
     * Basic steps are
     * - Detect which axis of the distance vector is longer (faster movement)
     * - for each pixel on the 'faster' axis calculate the position on the 'slower' axis.
     * Direction of the movement has to correctly considered
     * @param startPosition Starting position as a PositionVector
     * @param endPosition Ending position as a PositionVector
     * @return Intervening grid positions as a List of PositionVector's, including the starting and ending positions.
     */
    @Override
    public  List<PositionVector> calculatePath(PositionVector startPosition, PositionVector endPosition) {
        List<PositionVector> path = new ArrayList<>();
        PositionVector difference = PositionVector.subtract(endPosition, startPosition);
        PositionVector distance = new PositionVector(Math.abs(difference.getX()), Math.abs(difference.getY()));
        PositionVector direction = new PositionVector(Integer.signum(difference.getX()), Integer.signum(difference.getY()));

        // Determine which axis is the fast direction and set parallel/diagonal step values
        PositionVector parallelStep = new PositionVector();
        PositionVector diagonalStep;
        int distanceSlowAxis, distanceFastAxis;
        boolean isXAxisTheFastDirection = distance.getX() > distance.getY();
        if (isXAxisTheFastDirection) {
            parallelStep.setX(direction.getX());
            distanceSlowAxis = distance.getY();
            distanceFastAxis = distance.getX();
        } else {
            // y-axis is the 'fast' direction
            parallelStep.setY(direction.getY());
            distanceSlowAxis = distance.getX();
            distanceFastAxis = distance.getY();
        }
        diagonalStep = new PositionVector(direction);

        PositionVector currentStep = new PositionVector(startPosition);
        path.add(new PositionVector(currentStep));
        int error = distanceFastAxis / 2;

        for (int step = 0; step < distanceFastAxis; step++) {
            error -= distanceSlowAxis;
            if (error < 0) {
                error += distanceFastAxis;
                currentStep.addTogether(diagonalStep);
            } else {
                currentStep.addTogether(parallelStep);
            }
            path.add(new PositionVector(currentStep));
        }
        return path;
    }

    /**
     * Does indicate if a car would have a crash with a WALL space or another car at the given position.
     * @param carIndex The zero-based carIndex number
     * @param position A PositionVector of the possible crash position
     * @return A boolean indicator if the car would crash with a WALL or another car.
     */
    @Override
    public boolean willCarCrash(int carIndex, PositionVector position) {
        //check crash with wall
        if (track.getSpaceType(position) == Config.SpaceType.WALL){
            return true;
        }

        //check crash with car
        for(Car car : cars) {
            if(position.equals(car.getPosition()) && !car.equals(cars.get(carIndex))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Counts the amount of active cars in the game.
     *
     * @return true if the amount of active cars is greater or equal to 1
     */
    private boolean activeCarInGame() {
        int activeCarCount = 0;
        for(Car car : cars) {
            if(car.isActive()) activeCarCount++;
        }
        return activeCarCount >= 1;
    }

    /**
     * Checks if the game is still running.
     * @return true when the game should be continued
     */
    public boolean isRunning() {
        return activeCarInGame() && getWinner() == NO_WINNER;
    }
}
