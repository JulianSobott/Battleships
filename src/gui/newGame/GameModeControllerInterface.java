package gui.newGame;

import core.communication_data.GameSettings;

public interface GameModeControllerInterface {

    void linkToGodController(ControllerNewGame controllerNewGame);

    boolean validateSettings();

    void buildGameSettings(GameSettings gameSettings);
}
