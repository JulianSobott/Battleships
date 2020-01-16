package core.playgrounds;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.TurnResult;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface PlaygroundEnemy extends PlaygroundInterface{

    /**
     * The player that owns this playground, made a shot and got shotResult as result.
     *
     * @param shotResult
     */
    void update(ShotResult shotResult);

    boolean areAllShipsSunken();

    TurnResult.Error canShootAt(Position position);
}
