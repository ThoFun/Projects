package ch.zhaw.pm2.racetrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests theTests the functionality of the Bresenham-Algorithms
 */
public class BresenhamTest {
    private Track testTrack;
    private Game testGame;

    public BresenhamTest() throws InvalidTrackFormatException, FileNotFoundException {
        testTrack = new Track(new File("src/test/resources/quarter-mile-with-5-cars.txt"));
    }

    @BeforeEach
    void init(){
        testGame = new Game(testTrack);
    }

    /**
     * This tests if a path in the x-direction can be calculated.
     */
    @Test
    public void calculatePathOnlyXDirection(){
        PositionVector startPosition = new PositionVector(0, 1);
        PositionVector stopPosition = new PositionVector(5, 1);

        List<PositionVector> expectedList =
            List.of(
                startPosition,
                new PositionVector(1, 1),
                new PositionVector(2, 1),
                new PositionVector(3, 1),
                new PositionVector(4, 1),
                stopPosition);
        List<PositionVector> calculatedList = testGame.calculatePath(startPosition, stopPosition);

        assertEquals(expectedList, calculatedList);
    }

    /**
     * This tests if a path in the x-direction can be calculated.
     */
    @Test
    public void calculatePathOnlyYDirection(){
        PositionVector startPosition = new PositionVector(1, 0);
        PositionVector stopPosition = new PositionVector(1, 5);

        List<PositionVector> expectedList =
            List.of(
                startPosition,
                new PositionVector(1, 1),
                new PositionVector(1, 2),
                new PositionVector(1, 3),
                new PositionVector(1, 4),
                stopPosition);
        List<PositionVector> calculatedList = testGame.calculatePath(startPosition, stopPosition);

        assertEquals(expectedList, calculatedList);
    }

    /**
     * This tests the case of a diagonal path.
     */
    @Test
    public void testCalculatePathDiagonal(){
        PositionVector startPosition = new PositionVector(1, 0);
        PositionVector stopPosition = new PositionVector(5, 4);

        List<PositionVector> expectedList =
            List.of(
                startPosition,
                new PositionVector(2, 1),
                new PositionVector(3, 2),
                new PositionVector(4, 3),
                stopPosition);
        List<PositionVector> calculatedList = testGame.calculatePath(startPosition, stopPosition);

        assertEquals(expectedList, calculatedList);
    }
}
