package ch.zhaw.pm2.racetrack;

import ch.zhaw.pm2.racetrack.given.ConfigSpecification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config implements ConfigSpecification {

    // Directory containing the track files
    private File trackDirectory = new File("tracks");

    // Directory containing the track files
    private File moveDirectory = new File("moves");

    // Directory containing the follower files
    private File followerDirectory = new File("follower");

    /**
     * Gets the directory were the moves are saved.
     * @return directory.
     */
    public File getMoveDirectory() {
        return moveDirectory;
    }

    /**
     * Sets the directory.
     * @param moveDirectory directory of the moves.
     */
    public void setMoveDirectory(File moveDirectory) {
        Objects.requireNonNull(moveDirectory);
        this.moveDirectory = moveDirectory;
    }

    /**
     *  Gets the directory containing the follower files.
     * @return follower directory.
     */
    public File getFollowerDirectory() {
        return followerDirectory;
    }

    /**
     * Sets the directory containing the follower files.
     * @param followerDirectory directory of the follower.
     */
    public void setFollowerDirectory(File followerDirectory) {
        Objects.requireNonNull(followerDirectory);
        this.followerDirectory = followerDirectory;
    }

    /**
     * Gets the directory containing the track files.
     * @return track directory.
     */
    public File getTrackDirectory() {
        return trackDirectory;
    }

    /**
     * Sets the directory containing the track files.
     * @param trackDirectory directory of the tracks.
     */
    public void setTrackDirectory(File trackDirectory) {
        Objects.requireNonNull(trackDirectory);
        this.trackDirectory = trackDirectory;
    }

    /**
     * Gets a list of the current implemented strategies.
     * @return list of strategies
     */
    public List<StrategyType> getImplementedMoveStrategies() {
        List<StrategyType> strategies = new ArrayList<>();
        strategies.add(StrategyType.DO_NOT_MOVE);
        strategies.add(StrategyType.USER);

        return strategies;
    }

    /**
     * An enum of the characters on a track.
     */
    public enum SpaceType {
        WALL('#'),
        TRACK(' '),
        FINISH_UP('^'),
        FINISH_DOWN('v'),
        FINISH_LEFT('<'),
        FINISH_RIGHT('>');

        final char value;

        /**
         * Gets the SpaceType.
         * @param c a space type char
         */
        SpaceType(final char c) {
            value = c;
        }

        /**
         * Gets the value
         * @return value
         */
        public char getValue() {
            return value;
        }

        /**
         * Gets character and changes it to SpaceType.
         * @param character given character
         * @return SpaceType
         */
        public static SpaceType fromChar(char character) {
            for(SpaceType spaceType : SpaceType.values()) {
                if(spaceType.value == character) {
                    return spaceType;
                }
            }
            throw new IllegalArgumentException("No matching SpaceType for: " + character);
        }
    }
}
