package gui;

import core.utils.ResourcesDestructor;
import core.utils.logging.LoggerGUI;
import core.utils.logging.LoggerState;
import gui.WindowChange.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        LoggerState.info("Start Program");
        launch(args);
        LoggerState.info("End Program");
    }

    @Override
    public void start(Stage primaryStage) {
        LoggerGUI.info("Switch scene: None --> MainMenu");
        ControllerMainMenu controllerMainMenu = new ControllerMainMenu();
        SceneLoader.loadSceneInNewWindow(SceneLoader.GameScene.MAIN_MENU, controllerMainMenu, "Battleships");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        ResourcesDestructor.shutdownAll();
    }
}
