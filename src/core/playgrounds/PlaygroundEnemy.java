package core.playgrounds;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.communication_data.Position;
import core.communication_data.ShotResult;
import core.communication_data.TurnResult;

/**
 * Base interface for all Playgrounds, which are seen as the enemy playground inside a {@link core.Player}
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
public interface PlaygroundEnemy extends PlaygroundInterface{

    /**
     * The player that owns this playground, made a shot and got shotResult as result.
     *
     * @param shotResult Result with information what was hit
     */
    void update(ShotResult shotResult);

    /**
     * Method, to determine, whether a player has won.
     * @return true if all ships on the playground are sunken.
     */
    boolean areAllShipsSunken();

    /**
     * Validate, whether the position is valid and was not shot at before.
     * @param position Position where to shoot.
     * @return An result that has information what was the error or success.
     */
    TurnResult.Error canShootAt(Position position);
}
