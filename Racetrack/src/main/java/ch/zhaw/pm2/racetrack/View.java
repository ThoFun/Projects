package ch.zhaw.pm2.racetrack;

import java.io.File;

public interface View {
    PositionVector.Direction nextUserMover();
    File getTrackFile(File folder);
    Config.StrategyType getMoveStrategy(String carName);
}
