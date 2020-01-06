package gui.LoadGame;


import core.communication_data.LoadGameResult;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import gui.PlayGame.ControllerPlayGame;
import gui.WindowChange.SceneLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLoadGame implements Initializable {

    @FXML
    AnchorPane anchorPaneLoadGames;

    @FXML
    private ListView listViewSaveGames;

    MotionBlur motionBlur = new MotionBlur();

    private final AnchorPane ANCHORPANEMAINMENU;


    public ControllerLoadGame(AnchorPane anchorPaneMainMenu){

        this.ANCHORPANEMAINMENU = anchorPaneMainMenu;
        ANCHORPANEMAINMENU.setEffect(this.motionBlur);
    }



    /**
     *
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addSaveGamesToListView();

    }

    /**
     * add all Saved games to the ListView
     */

    private void addSaveGamesToListView() {


    }

    /**
     * loads the game for the player so that he's able to continue playing
     */

    @FXML
    private void LoadSaveGame() {

        closeWindow();
        // Hardcoded for debug purposes
        LoadGameResult res = GameSerialization.loadGame(1);
        if (res.getStatus() == LoadGameResult.LoadStatus.SUCCESS) {

            GameData gameData = res.getGameData();
            ControllerPlayGame controller = ControllerPlayGame.fromLoad(gameData);
            SceneLoader sceneLoader = new SceneLoader(this.ANCHORPANEMAINMENU, "../PlayGame/PlayGame.fxml", controller);
            sceneLoader.loadSceneInExistingWindow();
        } else {
            // TODO: inform user
        }

    }

    public void closeWindow(){

        ANCHORPANEMAINMENU.setEffect(null);
        Stage stage = (Stage) anchorPaneLoadGames.getScene().getWindow();
        stage.close();
    }

}
