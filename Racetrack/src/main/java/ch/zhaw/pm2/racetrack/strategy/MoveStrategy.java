package ch.zhaw.pm2.racetrack.strategy;

import static ch.zhaw.pm2.racetrack.PositionVector.Direction;

public interface MoveStrategy {
    Direction nextMove();
}
