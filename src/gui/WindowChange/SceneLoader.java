package gui.WindowChange;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

// TODO Singelton aus dieser klasse machen ??

public class SceneLoader {

    private Node node;
    private String filepath;
    private Object controllerClass;

    public SceneLoader(Node node, String filepath, Object controllerClass) {
        this.node = node;
        this.filepath = filepath;
        this.controllerClass = controllerClass;
    }


    public void loadSceneInExistingWindow() {

        Parent window = loadFxmlFile();
        Stage stage = (Stage) this.node.getScene().getWindow();
        stage.setScene(new Scene(window));


    }

    public void loadSceneInNewWindow(String windowTitle) {

        Parent window = loadFxmlFile();
        Stage stage = new Stage();
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(window));
        stage.show();
    }

    public void loadSceneInExistingWindowWithoutButtons(String windowTitle, Stage stageMainWindow) {

        Parent window = loadFxmlFile();
        Stage stage = new Stage();
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(window));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.resizableProperty().setValue(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        double x = stageMainWindow.getX();
        double y = stageMainWindow.getY();

        double width = stageMainWindow.getWidth();
        double height = stageMainWindow.getHeight();

        stage.setX( x + (width/2.5));
        stage.setY( y + (height/3) );

        stage.show();

    }

    // TODO Exption besser zurückgeben an der ganze aufruft ??

    private Parent loadFxmlFile() {

        Parent window = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.filepath));
            fxmlLoader.setController(controllerClass);
            window = fxmlLoader.load();
        } catch (IOException ioEx) {

            ioEx.printStackTrace();
        }
        return window;
    }

}
