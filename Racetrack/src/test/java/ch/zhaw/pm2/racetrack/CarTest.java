package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.strategy.DoNotMoveStrategy;
import ch.zhaw.pm2.racetrack.strategy.MoveStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests theTests the functionality of the class {@link Car}
 */
class CarTest{
    private final Track testTrack;
    private final Car firstCar;

    private Game testGame;

    public CarTest() throws InvalidTrackFormatException,FileNotFoundException{
        String testFilePath = "src/test/resources/quarter-mile-with-5-cars.txt";
        testTrack = new Track(new File (testFilePath));
        firstCar = testTrack.getCar(0);

    }

    @BeforeEach
    void init() {
        testGame = new Game(testTrack);
    }

    /**
     * This tests if the car can be set on a position.
     */
    @Test
    void testSetCarPosition(){
        PositionVector positionVector = new PositionVector(56, 2);
        firstCar.setPosition(positionVector);
        assertEquals(positionVector, firstCar.getPosition());
    }

    /**
     * This tests if the correct IDs are given to the cars.
     */
    @Test
    void testCarID(){
        assertEquals('a', testTrack.getCarId(0));
        assertEquals('b', testTrack.getCarId(1));
        assertEquals('c', testTrack.getCarId(2));
        assertEquals('d', testTrack.getCarId(3));
        assertEquals('e', testTrack.getCarId(4));
    }

    /**
     * This tests if the move strategy can be set to a car.
     */
    @Test
    void testSetMoveStrategy(){
        MoveStrategy moveStrategy = new DoNotMoveStrategy();
        assertNull(firstCar.getMoveStrategy());
        firstCar.setMoveStrategy(moveStrategy);
        assertEquals(moveStrategy, firstCar.getMoveStrategy());
    }

    /**
     * This tests the ID range.
     */
    @Test
    void testCarIDOutOfBounds(){
        assertThrows(IndexOutOfBoundsException.class, () -> testGame.getCarId(5));
    }

    /**
     * This tests the acceleration of a car.
     */
    @Test
    void testCarAcceleration(){
        firstCar.accelerate(PositionVector.Direction.DOWN);
        assertEquals(PositionVector.Direction.DOWN.vector, firstCar.getVelocity());
    }

    /**
     * This tests the acceleration of a car, but with a negative number.
     */
    @Test
    void testCarAccelerationNegative(){
        firstCar.accelerate(PositionVector.Direction.UP);
        assertEquals(PositionVector.Direction.UP.vector, firstCar.getVelocity());
    }

    /**
     * This tests if a car can go up left.
     */
    @Test
    void testDirectionUpLeft(){
        firstCar.setPosition(new PositionVector(1,1));
        firstCar.accelerate(PositionVector.Direction.UP_LEFT);
        firstCar.move();
        assertEquals(new PositionVector(0,0), firstCar.getPosition());
    }

    /**
     *This tests if a car can go up.
     */
    @Test
    void testDirectionUp(){
        firstCar.setPosition(new PositionVector(0,1));
        firstCar.accelerate(PositionVector.Direction.UP);
        firstCar.move();
        assertEquals(new PositionVector(0,0), firstCar.getPosition());
    }

    /**
     *This tests if a car can go up right.
     */
    @Test
    void testDirectionUpRight(){
        firstCar.setPosition(new PositionVector(0,1));
        firstCar.accelerate(PositionVector.Direction.UP_RIGHT);
        firstCar.move();
        assertEquals(new PositionVector(1,0), firstCar.getPosition());
    }

    /**
     *This tests if a car can go to the left.
     */
    @Test
    void testDirectionsMiddleLeft(){
        firstCar.setPosition(new PositionVector(1,0));
        firstCar.accelerate(PositionVector.Direction.LEFT);
        firstCar.move();
        assertEquals(new PositionVector(0,0), firstCar.getPosition());
    }

    /**
     * This tests if a car will not move.
     */
    @Test
    public void testDirectionsMiddle(){
        firstCar.setPosition(new PositionVector(0,0));
        firstCar.accelerate(PositionVector.Direction.NONE);
        firstCar.move();
        assertEquals(new PositionVector(0,0), firstCar.getPosition());
    }

    /**
     * This tests if a car can go to the right.
     */
    @Test
    void testDirectionsMiddleRight(){
        firstCar.setPosition(new PositionVector(0,0));
        firstCar.accelerate(PositionVector.Direction.RIGHT);
        firstCar.move();
        assertEquals(new PositionVector(1,0), firstCar.getPosition());
    }

    /**
     * This tests if a car can go down left.
     */
    @Test
    void testDirectionsDownLeft(){
        firstCar.setPosition(new PositionVector(1,0));
        firstCar.accelerate(PositionVector.Direction.DOWN_LEFT);
        firstCar.move();
        assertEquals(new PositionVector(0,1), firstCar.getPosition());
    }

    /**
     * This tests if a car can go down.
     */
    @Test
    void testDirectionsDown(){
        firstCar.setPosition(new PositionVector(0,0));
        firstCar.accelerate(PositionVector.Direction.DOWN);
        firstCar.move();
        assertEquals(new PositionVector(0,1), firstCar.getPosition());
    }

    /**
     * This tests if a car can go down right.
     */
    @Test
    void testDirectionsDownRight(){
        firstCar.setPosition(new PositionVector(0,0));
        firstCar.accelerate(PositionVector.Direction.DOWN_RIGHT);
        firstCar.move();
        assertEquals(new PositionVector(1,1), firstCar.getPosition());
    }

    /**
     * This tests the cars velocity.
     */
    @Test
    void testCarVelocity(){
        testGame.doCarTurn(PositionVector.Direction.RIGHT);
        assertEquals(PositionVector.Direction.RIGHT.vector, testGame.getCarVelocity(testGame.getCurrentCarIndex()));
    }

    /**
     * This tests if a car can crash.
     */
    @Test
    void testCrash(){
        assertFalse(firstCar.isCrashed());
        firstCar.crash();
        assertTrue(firstCar.isCrashed());
    }

    /**
     * This tests when a car has crashed, if an indicator will replace the ID of the car with an X.
     */
    @Test
    void testToStringCrashIndicator(){
        firstCar.crash();
        assertEquals(String.valueOf(Track.CRASH_INDICATOR), firstCar.toString());
    }

    /**
     * This tests if the toString method returns the car ID if the car isn't crashed.
     */
    @Test
    void testToStringGetID(){
        assertEquals(String.valueOf(firstCar.getId()), firstCar.toString());
    }
}
