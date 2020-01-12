package gui.PlayGame;

/**
 * Allow connection to the GUI
 */
public interface InGameGUI {

    /**
     * poll for new events and display them in the GUI
     */
    void eventUpdaterThread();

    /**
     * Add a new save Game event
     * @param id SaveGame must have this id
     */
    void saveGame(long id);
}
