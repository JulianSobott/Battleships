package gui.WindowChange;

import core.utils.logging.LoggerGUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SceneLoader {

    private static SceneLoader instance;
    private Scene rootScene;

    public SceneLoader() {
    }

    public static void loadSceneInNewWindow(GameScene gameScene, Object controller, String title) {
        instance = new SceneLoader();
        Parent window = instance.loadFxmlFile(gameScene, controller);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setMinWidth(500);
        stage.setMinHeight(600);
        stage.getIcons().add(new Image(SceneLoader.class.getResourceAsStream("/images/General/window_icon.png")));
        stage.setScene(new Scene(window));
        stage.show();
        instance.rootScene = stage.getScene();
    }

    public static void loadSceneInExistingWindow(GameScene gameScene, Object controller) {
        Parent window = instance.loadFxmlFile(gameScene, controller);
        instance.rootScene.setRoot(window);
    }


    public static void loadSceneInExistingWindowWithoutButtons(GameScene gameScene, Object controller,
                                                               String windowTitle) {

        Parent window = instance.loadFxmlFile(gameScene, controller);
        Stage stage = new Stage();
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(window));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.resizableProperty().setValue(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        double x = instance.rootScene.getX();
        double y = instance.rootScene.getY();

        double width = instance.rootScene.getWidth();
        double height = instance.rootScene.getHeight();

        stage.setX(x + (width / 2.5));
        stage.setY(y + (height / 3));

        stage.show();

    }

    // TODO Exption besser zurückgeben an der ganze aufruft ??

    private Parent loadFxmlFile(GameScene gameScene, Object controllerClass) {

        Parent window = null;
        String filePath = "/gui/" + gameScene.fxmlPath;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(filePath));
            fxmlLoader.setController(controllerClass);
            window = fxmlLoader.load();
        } catch (IOException ioEx) {
            LoggerGUI.error("Cannot load fxml file: filepath=" + filePath);
            ioEx.printStackTrace();
        }
        return window;
    }

    public enum GameScene {
        GAME_OVER("GameOver/GameOver"),
        LOAD_GAME("LoadGame/LoadGame"),
        NEW_GAME("newGame/NewGame"),
        PLAY_GAME("PlayGame/PlayGame"),
        SETTINGS("Settings/Settings"),
        SHIP_PLACEMENT("ShipPlacement/ShipPlacement"),
        MAIN_MENU("Main_Menu"),
        NEW_GAME_TEST("newGame/NewGame"),
        ;

        private String fxmlPath;

        GameScene(String fxmlPath) {
            this.fxmlPath = fxmlPath + ".fxml";
        }
    }

}
