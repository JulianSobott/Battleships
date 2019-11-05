package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Parent window = null;
        try {
            window = FXMLLoader.load(getClass().getResource("Main_Menu.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(window));
            stage.setTitle("Battleship");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
