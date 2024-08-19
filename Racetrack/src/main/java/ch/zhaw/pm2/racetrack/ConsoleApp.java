package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.strategy.*;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class ConsoleApp {
    private final Config config = new Config();
    private final File trackPath = config.getTrackDirectory();
    private final ConsoleView view;
    private final Game game;

    public static void main(String[] args){
        ConsoleApp app = new ConsoleApp();
        app.startGame();
    }

    public ConsoleApp() {
        TextIO textIO = TextIoFactory.getTextIO();
        view = new ConsoleView(textIO, config);
        view.printWelcomeText();
        Track track = getInitialTrack();
        view.setTrack(track);
        game = new Game(track);
    }

    /**
     * Starts the game RaceTrack.
     */
    private void startGame() {
        view.clearTextTerminal();
        while(!setMoveStrategies(game.getTrack().getCars())) {
            view.print("Strategies couldn't be set, try again..");
        }
        view.clearTextTerminal();

        while (game.isRunning()) {
            view.printTrack();
            Car activeCar = game.getActiveCar();

            if(activeCar.getMoveStrategy() instanceof UserMoveStrategy) {
                view.print("Car " + activeCar + " it's your turn:\n");
            }

            PositionVector.Direction nextMove = activeCar.getMoveStrategy().nextMove();
            game.doCarTurn(nextMove);
            view.clearTextTerminal();

            if(game.getWinner() != Game.NO_WINNER) {
                view.printWinner(game.getTrack().getCar(game.getWinner()).getId());
            } else game.switchToNextActiveCar();
        }

        if(game.getWinner() == Game.NO_WINNER) {
            view.printNoWinner();
        }

        view.printTrack();
        view.waitForExit();
    }

    /**
     * Sets the specified move strategy for the car
     * @param cars list of cars in game
     * @return true, when setting strategy is successful, otherwise false
     */
    private boolean setMoveStrategies(List<Car> cars) {
        for(Car car : cars) {
            switch (view.getMoveStrategy(String.valueOf(car.getId()))) {
                case USER -> car.setMoveStrategy(new UserMoveStrategy(view));
                case MOVE_LIST -> car.setMoveStrategy(new MoveListStrategy());
                case DO_NOT_MOVE -> car.setMoveStrategy(new DoNotMoveStrategy());
                case PATH_FOLLOWER -> car.setMoveStrategy(new PathFollowerMoveStrategy());
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the chosen track.
     * @return track
     */
    private Track getInitialTrack() {
        Track track;
        try {
            track = new Track(view.getTrackFile(trackPath));
        } catch (InvalidTrackFormatException e) {
            view.print(e.getMessage());
            return getInitialTrack();
        } catch (FileNotFoundException e) {
            view.print("File can't be found");
            return getInitialTrack();
        }
        return track;
    }
}
