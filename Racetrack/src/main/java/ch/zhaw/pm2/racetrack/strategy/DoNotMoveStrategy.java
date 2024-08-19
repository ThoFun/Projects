package ch.zhaw.pm2.racetrack.strategy;

import static ch.zhaw.pm2.racetrack.PositionVector.Direction;

/**
 * Do not accelerate in any direction.
 */
public class DoNotMoveStrategy implements MoveStrategy {

    /**
     *This methode sets the car to not move
     * @return direction set to none
     */
    @Override
    public Direction nextMove() {
        return Direction.NONE;
    }
}
