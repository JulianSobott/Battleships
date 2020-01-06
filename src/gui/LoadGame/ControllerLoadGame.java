package gui.LoadGame;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLoadGame implements Initializable {

    @FXML
    AnchorPane anchorPaneLoadGames;

    @FXML
    private ListView listViewSaveGames;


    /**
     *
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addSaveGamesToListView();

    }

    /**
     *  add all Saved games to the ListView
     */

    private void addSaveGamesToListView(){


    }

    /**
     * loads the game for the player so that he's able to continue playing
     */

    @FXML
    private void LoadSaveGame(){


    }

}
