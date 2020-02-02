package gui.UiClasses;


import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Notification {

    Popup popup;
    private String text = "";
    private Stage stage;
    private Label lblHeader;
    private Label lblContent;
    private ImageView notificationIconView;
    private Pane rightSide;
    private Pane leftSide;

    private NotificationLevel notificationLevel;
    private long autoHideMillis;
    private boolean autoHide = false;

    private Notification(Stage stage) {
        this.stage = stage;
        this.popup = new Popup();
        this.notificationLevel = NotificationLevel.NONE;

        Pane root = new HBox();
        root.setId("root");
        stage.getScene().getStylesheets().add("/styles/notifications.css");
        // left side
        leftSide = new VBox();
        leftSide.setId("leftSide");
        VBox innerLeft = new VBox();
        innerLeft.setPrefHeight(50000); // fit size of parent
        innerLeft.setAlignment(Pos.CENTER);
        notificationIconView = new ImageView();
        notificationIconView.setId("level_icon");
        notificationIconView.setPreserveRatio(true);
        notificationIconView.setFitWidth(30);
        innerLeft.getChildren().addAll(notificationIconView);
        leftSide.getChildren().addAll(innerLeft);


        // Right side
        rightSide = new VBox();
        rightSide.setId("rightSide");
        HBox header = new HBox();
        header.setId("header");
        this.lblHeader = new Label();
        header.getChildren().addAll(this.lblHeader);

        Pane content = new HBox();
        content.setId("content");
        this.lblContent = new Label();
        lblContent.setWrapText(true);
        content.getChildren().add(lblContent);
        rightSide.getChildren().addAll(header, content);

        root.getChildren().addAll(leftSide, rightSide);
        popup.getContent().add(root);
    }

    public static Notification create(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        return new Notification(stage);
    }

    public Notification header(String text) {
        this.lblHeader.setText(text);
        return this;
    }

    public Notification level(NotificationLevel level) {
        this.notificationLevel = level;
        return this;
    }

    public Notification text(String text) {
        this.text = text;
        lblContent.setText(this.text);
        return this;
    }

    public Notification autoHide(long millis) {
        this.autoHide = true;
        this.autoHideMillis = millis;
        return this;
    }

    public void show() {
        // Level
        if (this.notificationLevel != NotificationLevel.NONE) {
            Image image = new Image("/images/icons/" + notificationLevel.imageName);
            notificationIconView.setImage(image);
        }
        rightSide.setStyle("-fx-background-color: " +
                Color.web(notificationLevel.color).brighter().toString().replace("0x", "#"));
        leftSide.setStyle("-fx-background-color: " +
                Color.web(notificationLevel.color).darker().toString().replace("0x", "#"));

        popup.show(this.stage);
        if (autoHide) {
            Task<Void> hider = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        Thread.sleep(autoHideMillis);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            };
            hider.setOnSucceeded(ev -> {
                this.hide();
            });
            new Thread(hider).start();
        }
    }

    public void hide() {
        popup.hide();
    }

    public void setY(double y) {
        popup.setY(y);
    }

    public enum NotificationLevel {
        WARNING("warning", "orange"),
        INFO("info", "#13c9a8"),
        ERROR("error", "red"),
        DONE("done", "green"),
        NONE("info", "grey");

        String imageName;
        String color;

        NotificationLevel(String imageName, String color) {
            this.imageName = "baseline_" + imageName + "_black_48dp.png";
            this.color = color;
        }
    }
}
