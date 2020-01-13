package core;

import core.communication_data.Position;
import core.communication_data.ShotResult;

public class PlaygroundOwnBuildUp extends PlaygroundBuildUp implements PlaygroundOwn {

    public PlaygroundOwnBuildUp(int size) {
        super(size);
    }

    @Override
    public ShotResult gotHit(Position position) {
        return null;
    }
}
