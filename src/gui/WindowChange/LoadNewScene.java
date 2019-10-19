package gui.WindowChange;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoadNewScene {

    private Node node;
    private String filepath;
    private Object controllerClass;

    public LoadNewScene(Node node, String filepath, Object controllerClass) {
        this.node = node;
        this.filepath = filepath;
        this.controllerClass = controllerClass;
    }

    public void load() {

        Parent window = null;

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.filepath));
            fxmlLoader.setController(controllerClass);

            window = fxmlLoader.load();
            Stage stage = (Stage) this.node.getScene().getWindow();

            stage.setScene(new Scene(window));

        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }
}
