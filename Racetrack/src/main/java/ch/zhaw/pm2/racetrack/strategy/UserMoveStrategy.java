package ch.zhaw.pm2.racetrack.strategy;

import ch.zhaw.pm2.racetrack.PositionVector.Direction;
import ch.zhaw.pm2.racetrack.View;

/**
 * Let the user decide the next move.
 */
public class UserMoveStrategy implements MoveStrategy {
    private final View view;

    public UserMoveStrategy(View view) {
        this.view = view;
    }

    /**
     * Gets next move.
     * @return next move of user.
     */
    @Override
    public Direction nextMove() {
        return view.nextUserMover();
    }
}
