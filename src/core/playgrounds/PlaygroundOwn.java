package core.playgrounds;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.communication_data.Position;
import core.communication_data.ShotResult;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface PlaygroundOwn extends PlaygroundInterface{

    ShotResult gotHit(Position position);
}
