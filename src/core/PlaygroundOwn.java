package core;

import core.communication_data.Position;
import core.communication_data.ShotResult;

public interface PlaygroundOwn extends PlaygroundInterface{

    ShotResult gotHit(Position position);
}
