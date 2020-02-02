package gui.LoadGame;


import core.Player;
import core.communication_data.LoadGameResult;
import core.serialization.GameData;
import core.serialization.GameSerialization;
import core.utils.logging.LoggerGUI;
import gui.PlayGame.ControllerPlayGame;
import gui.UiClasses.Notification;
import gui.WindowChange.SceneLoader;
import gui.newGame.ControllerNewGame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import player.PlayerNetwork;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerLoadGame implements Initializable {

    @FXML
    AnchorPane anchorPaneLoadGames;

    @FXML
    private ListView listViewSaveGames;

    private ObservableList<String> itemsSaveGame = FXCollections.observableArrayList();
    private ArrayList<GameData> saveGames = new ArrayList<>();

    private final AnchorPane ANCHORPANEMAINMENU;


    public ControllerLoadGame(AnchorPane anchorPaneMainMenu) {

        MotionBlur motionBlur = new MotionBlur();
        this.ANCHORPANEMAINMENU = anchorPaneMainMenu;
        ANCHORPANEMAINMENU.setEffect(motionBlur);
    }


    /**
     *
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addSaveGamesToListView();
        listViewSaveGames.setItems(itemsSaveGame);
        listViewSaveGames.setId("listView");
    }

    /**
     * add all Saved games to the ListView
     */

    private void addSaveGamesToListView() {

        List<GameData> arrayListGameData = GameSerialization.getAllSaveGames();

        for (GameData saveGame : arrayListGameData) {

            itemsSaveGame.add(saveGame.getTimestamp());
            saveGames.add(saveGame);
        }
    }

    /**
     * loads the game for the player so that he's able to continue playing
     */

    @FXML
    private void LoadSaveGame() {

        int index = getSelectedSaveGame();
        if (index == -1) {
            return;
        }
        long id = saveGames.get(index).getGameID();
        Player[] ps = saveGames.get(index).getPlayers();
        closeWindow();
        LoadGameResult res = GameSerialization.loadGame(id);
        if (res.getStatus() == LoadGameResult.LoadStatus.SUCCESS) {

            GameData gameData = res.getGameData();
            if (ps[1].getClass() == PlayerNetwork.class) {
                LoggerGUI.info("Switch scene: LoadGame --> NewGame");
                ControllerNewGame controllerNewGame = new ControllerNewGame();
                SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.NEW_GAME, controllerNewGame);
                controllerNewGame.initFromNetworkLoaded(res.getGameData());
                return;
            }

            LoggerGUI.info("Switch scene: LoadGame --> PlayGame");
            ControllerPlayGame controller = ControllerPlayGame.fromLoad(gameData);
            SceneLoader.loadSceneInExistingWindow(SceneLoader.GameScene.PLAY_GAME, controller);
            controller.initFieldsFromLoad(gameData);
        } else {
            // TODO: inform user
        }

    }

    /**
     * Returns the desired index of the score to be loaded
     */

    private int getSelectedSaveGame() {

        int index;
        if (listViewSaveGames.getSelectionModel().getSelectedIndex() == -1) {
            Notification.create(anchorPaneLoadGames)
                    .header("Kein Spiel ausgewählt")
                    .text("Bitte wähle ein Spiel aus")
                    .level(Notification.NotificationLevel.WARNING)
                    .autoHide(3000)
                    .show();

            index = -1;
        } else {
            index = listViewSaveGames.getSelectionModel().getSelectedIndex();
        }
        return index;
    }

    /**
     * closes the window in which the scores are selected
     */

    public void closeWindow() {
        LoggerGUI.info("Switch scene: LoadGame --> MainMenu");
        ANCHORPANEMAINMENU.setEffect(null);
        Stage stage = (Stage) anchorPaneLoadGames.getScene().getWindow();
        stage.close();
    }

}
