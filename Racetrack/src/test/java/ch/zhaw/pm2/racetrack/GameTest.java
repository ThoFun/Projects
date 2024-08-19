package ch.zhaw.pm2.racetrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of the class{@link Game}
 */
class GameTest {
    private Track testTrack;
    private Game testGame;

    public GameTest() throws InvalidTrackFormatException, FileNotFoundException {
        testTrack = new Track(new File("src/test/resources/quarter-mile-with-5-cars.txt"));
    }

    @BeforeEach
    void init(){
        testGame = new Game(testTrack);
    }

    /**
     * This tests if a track can be set correctly.
     */
    @Test
    void testSetNewTrack(){
        assertEquals(testTrack, testGame.getTrack());
    }

    /**
     * This tests if a car position can be set and retrieved correctly.
     */
    @Test
    void testGetCarPosition() {
        Car car = testGame.getActiveCar();
        PositionVector positionVector = new PositionVector(56, 2);
        car.setPosition(positionVector);
        assertEquals(positionVector, testGame.getActiveCar().getPosition());
    }

    /**
     * This tests if the next position of a car is correctly calculated.
     */
    @Test
    void testCarNextPosition(){
        Car testFirstCar = testTrack.getCar(0);
        PositionVector carAcceleration = new PositionVector(PositionVector.Direction.RIGHT.vector);

        PositionVector expectedCarPosition = new PositionVector(
            testFirstCar.getPosition().getX() + carAcceleration.getX(),
            testFirstCar.getPosition().getY() + carAcceleration.getY());

        testFirstCar.accelerate(PositionVector.Direction.RIGHT);
        PositionVector calculatedCarPosition = testFirstCar.nextPosition();

        assertEquals(calculatedCarPosition, expectedCarPosition);
    }

    /**
     * This tests if the game correctly switches to the next car.
     */
    @Test
    void testSwitchingCars(){
        int expectedIndex = testGame.getCurrentCarIndex() + 1;
        testGame.switchToNextActiveCar();
        assertEquals(expectedIndex, testGame.getCurrentCarIndex());
    }

    /**
     * This tests if the current game is still running after initialisation.
     */
    @Test
    void testGameStillRunningAfterInitialisation(){
        assertTrue(testGame.isRunning());
    }

    /**
     * This tests if the getWinner methode retrieves no winner after initialisation.
     */
    @Test
    void testGetNoWinner(){
        assertEquals(Game.NO_WINNER, testGame.getWinner());
    }

    /**
     * This tests if the getWinner methode retrieves the winner.
     */
    @Test
    void testGetWinner(){
        testGame.getActiveCar().setWinner();
        assertEquals(testGame.getCurrentCarIndex(), testGame.getWinner());
    }

    /**
     * This tests how a car wins, by going over the finish line
     */
    @Test
    void testGetWinnerOverFinishLine() {
        while (testGame.getWinner() == Game.NO_WINNER){
            testGame.doCarTurn(PositionVector.Direction.LEFT);
        }
        assertEquals(testGame.getCurrentCarIndex(), testGame.getWinner());
    }

    /**
     * This tests if it can register a win when the car passes the finish line going to the right.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testGetWinnerOverFinishLineRight() throws InvalidTrackFormatException, FileNotFoundException {
        testGame = new Game(new Track(new File("src/test/resources/quarter-mile-test-finish-line-right.txt")));
        while (testGame.getWinner() == Game.NO_WINNER){
            testGame.doCarTurn(PositionVector.Direction.RIGHT);
        }
        assertEquals(testGame.getCurrentCarIndex(), testGame.getWinner());
    }

    /**
     *This tests if it can register a win when the car passes the finish line going to the left.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testGetWinnerOverFinishLineLeft() throws InvalidTrackFormatException, FileNotFoundException {
        testGame = new Game(new Track(new File("src/test/resources/quarter-mile-test-finish-line-left.txt")));
        while (testGame.getWinner() == Game.NO_WINNER){
            testGame.doCarTurn(PositionVector.Direction.LEFT);
        }
        assertEquals(testGame.getCurrentCarIndex(), testGame.getWinner());
    }

    /**
     * This tests if it can register a win when the car passes the finish line going up.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testGetWinnerOverFinishLineUp() throws InvalidTrackFormatException, FileNotFoundException {
        testGame = new Game(new Track(new File("src/test/resources/quarter-mile-test-finish-line-up.txt")));
        while (testGame.getWinner() == Game.NO_WINNER){
            testGame.doCarTurn(PositionVector.Direction.UP);
        }
        assertEquals(testGame.getCurrentCarIndex(), testGame.getWinner());

    }

    /**
     * This tests if it can register a win when the car passes the finish line going down.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testGetWinnerOverFinishLineDown() throws InvalidTrackFormatException, FileNotFoundException {
        testGame = new Game(new Track(new File("src/test/resources/quarter-mile-test-finish-line-down.txt")));
        while (testGame.getWinner() == Game.NO_WINNER){
            testGame.doCarTurn(PositionVector.Direction.DOWN);
        }
        assertEquals(testGame.getCurrentCarIndex(), testGame.getWinner());
    }

    /**
     * Crashes all cars except of one and checks if the not crashed car is the winner.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testGetWinnerTroughCrashingAllCarsButOne() throws InvalidTrackFormatException, FileNotFoundException {
        Track track = new Track(new File("src/test/resources/quarter-mile-with-5-cars.txt"));
        testGame = new Game(track);
        int firstCarIndex = 0;

        for (int i = 0; i < track.getCarCount() ; i++) {
            if (testGame.getCurrentCarIndex() != firstCarIndex) {
                track.getCar(i).crash();
            }
            testGame.switchToNextActiveCar();
        }

        assertEquals(firstCarIndex, testGame.getWinner());
    }

    /**
     * This tests if the game can retrieve a crashed car.
     */
    @Test
    void testGetCrashedCar(){
        Car testFirstCar = testGame.getActiveCar();
        testFirstCar.crash();
        assertTrue(testFirstCar.isCrashed());
    }

    /**
     * This tests if a car can crash with a wall.
     */
    @Test
    void testCarCrashedWithWall(){
        Car testFirstCar = testGame.getActiveCar();
        testGame.doCarTurn(PositionVector.Direction.UP_RIGHT);
        assertTrue(testFirstCar.isCrashed());
    }

    /**
     * This tests if a car can crash with an already crashed car.
     */
    @Test
    void testCarCrashingWithCrashedCar(){
        Car testFirstCar = testGame.getActiveCar();
        Car testSecondCar = testTrack.getCar(testGame.getCurrentCarIndex()+1);
        testSecondCar.crash();
        testGame.doCarTurn(PositionVector.Direction.DOWN);
        assertTrue(testFirstCar.isCrashed() && testSecondCar.isCrashed());
    }

    /**
     * This tests cars can crash into each other.
     */
    @Test
    void testCarCrashingWithOtherCar(){
        Car testFirstCar = testGame.getActiveCar();
        Car testSecondCar = testTrack.getCar(testGame.getCurrentCarIndex()+1);
        testGame.doCarTurn(PositionVector.Direction.DOWN);
        assertTrue(testFirstCar.isCrashed() && !testSecondCar.isCrashed());
    }

    /**
     * Car crashes if he moves outside the board
     */
    @Test
    void testCarCrashOutOfBounds() throws InvalidTrackFormatException, FileNotFoundException {
        Track track = new Track(new File("src/test/resources/quarter-mile-no-border.txt"));
        Game game = new Game(track);
        Car car = game.getActiveCar();
        game.doCarTurn(PositionVector.Direction.RIGHT);
        game.doCarTurn(PositionVector.Direction.RIGHT);
        assertTrue(car.isCrashed());
    }

    /**
     * This tests checks that a car stops on the finish line and doesn't crash behind
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testNoCrashAfterFinishLine() throws InvalidTrackFormatException, FileNotFoundException {
        testGame = new Game(new Track(new File("src/test/resources/track-crash-finish-line.txt")));
        Car testFirstCar = testGame.getActiveCar();
        while (testGame.getWinner() == Game.NO_WINNER){
            testGame.doCarTurn(PositionVector.Direction.LEFT);
        }
        assertFalse(testFirstCar.isCrashed());
    }

    /**
     * This tests if a car won't crash if it crosses the finish line from the wrong direction.
     * In this test car will come from the left and will go right.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testCarsCrashingComingFromRightToLeftFinishLine() throws InvalidTrackFormatException, FileNotFoundException {
        testTrack = new Track(new File("src/test/resources/finish-line-crashes-right.txt"));
        testGame = new Game(testTrack);
        Car firstCar = testGame.getActiveCar();
        testGame.doCarTurn(PositionVector.Direction.RIGHT);
        assertTrue(firstCar.isCrashed());
    }

    @Test
    void testCarsCrashingComingFromLeftToRightFinishLine() throws InvalidTrackFormatException, FileNotFoundException {
        testTrack = new Track(new File("src/test/resources/finish-line-crashes-left.txt"));
        testGame = new Game(testTrack);
        Car firstCar = testGame.getActiveCar();
        testGame.doCarTurn(PositionVector.Direction.LEFT);
        assertTrue(firstCar.isCrashed());
    }

    /**
     * This tests if a car will crash if it crosses the finish line from the wrong direction.
     * In this test car will come from the top and will go down.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testCarsCrashingComingDownFinishLine() throws InvalidTrackFormatException, FileNotFoundException {
        testTrack = new Track(new File("src/test/resources/finish-line-crashes-down.txt"));
        testGame = new Game(testTrack);
        Car firstCar = testTrack.getCar(0);
        testGame.doCarTurn(PositionVector.Direction.DOWN);
        assertTrue(firstCar.isCrashed());
    }

    /**
     * This tests if a car will crash if it crosses the finish line from the wrong direction.
     * In this test car will come from the bottom and will go up.
     * @throws InvalidTrackFormatException if the trackFormat is invalid
     * @throws FileNotFoundException if the given File can't be found
     */
    @Test
    void testCarsCrashingComingUpFinishLine() throws InvalidTrackFormatException, FileNotFoundException {
        testTrack = new Track(new File("src/test/resources/finish-line-crashes-up.txt"));
        testGame = new Game(testTrack);
        Car firstCar = testTrack.getCar(0);
        testGame.doCarTurn(PositionVector.Direction.UP);
        assertTrue(firstCar.isCrashed());
    }

    /**
     * This tests the switchToNextCar will skip a crashed car.
     */
    @Test
    void testSwitchToNextCarSkippingCrashedCar(){
        Car toBeSkippedCar = testTrack.getCar(testGame.getCurrentCarIndex()+1);
        toBeSkippedCar.crash();
        testGame.switchToNextActiveCar();
        assertNotEquals(toBeSkippedCar, testGame.getActiveCar());
    }
}
