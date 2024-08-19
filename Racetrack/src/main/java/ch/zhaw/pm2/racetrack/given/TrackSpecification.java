package ch.zhaw.pm2.racetrack.given;

import ch.zhaw.pm2.racetrack.Config;
import ch.zhaw.pm2.racetrack.PositionVector;

/**
 * This interface specifies stuff we use to test Racetrack for grading. It shall not be altered!
 */
public interface TrackSpecification {
    Config.SpaceType getSpaceType(PositionVector position);

    int getCarCount();

    CarSpecification getCar(int carIndex);

    char getCarId(int carIndex);

    PositionVector getCarPos(int carIndex);

    PositionVector getCarVelocity(int carIndex);

    char getCharAtPosition(int y, int x, Config.SpaceType currentSpace);

    String toString();
}
