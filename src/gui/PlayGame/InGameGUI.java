package gui.PlayGame;

/**
 * Allow connection to the GUI
 */
public interface InGameGUI {

    /**
     * Add a new save Game event
     * @param id SaveGame must have this id
     */
    void saveGame(long id);
}
