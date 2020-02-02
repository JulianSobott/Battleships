package gui_tests;

import gui.UiClasses.Notification;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TestNotification extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("Click");

        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {
                        Notification n = Notification.create(button)
                                .text("Hello World")
                                .header("Some header")
                                .level(Notification.NotificationLevel.INFO)
                                .autoHide(1000);
                        n.show();
                    }
                };


        button.setOnAction(event);

        HBox box = new HBox();
        box.getChildren().add(button);

        primaryStage.setScene(new Scene(box, 200, 200));
        primaryStage.show();


        Notification n = Notification.create(button)
                .text("Please fill all fields")
                .header("Not all fields filled!")
                .level(Notification.NotificationLevel.WARNING)
                .autoHide(300000);
        n.setY(0);
        n.show();

        Notification n1 = Notification.create(button)
                .text("Please fill all fields")
                .header("Not all fields filled!")
                .level(Notification.NotificationLevel.ERROR)
                .autoHide(300000);
        n1.setY(100);
        n1.show();

        Notification n2 = Notification.create(button)
                .text("Please fill all fields")
                .header("Not all fields filled!")
                .level(Notification.NotificationLevel.INFO)
                .autoHide(300000);
        n2.setY(200);
        n2.show();

        Notification n3 = Notification.create(button)
                .text("Please fill all fields")
                .header("Not all fields filled!")
                .level(Notification.NotificationLevel.NONE)
                .autoHide(300000);
        n3.setY(300);
        n3.show();

        Notification n4 = Notification.create(button)
                .text("Please fill all fields")
                .header("Not all fields filled!")
                .level(Notification.NotificationLevel.DONE)
                .autoHide(300000);
        n4.setY(400);
        n4.show();
    }
}

