package ch.zhaw.pm2.racetrack.given;

import ch.zhaw.pm2.racetrack.PositionVector;

import java.util.List;

/**
 * This interface specifies stuff we use to test Racetrack for grading. It shall not be altered!
 */
public interface GameSpecification {
    int getCurrentCarIndex();

    char getCarId(int carIndex);

    PositionVector getCarPosition(int carIndex);

    PositionVector getCarVelocity(int carIndex);

    int getWinner();

    void doCarTurn(PositionVector.Direction acceleration);

    void switchToNextActiveCar();

    List<PositionVector> calculatePath(PositionVector startPosition, PositionVector endPosition);

    boolean willCarCrash(int carIndex, PositionVector position);
}
