package gui.custom_elements;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;

public class HelpWidget extends VBox {

    @FXML
    private Button toggleButton;

    private boolean currentVisible = false;
    private Popup popup = new Popup();
    private TextArea textArea = new TextArea();

    private DoubleProperty textAreaWidth = new SimpleDoubleProperty();
    private DoubleProperty textAreaHeight = new SimpleDoubleProperty();

    public HelpWidget() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/custom_elements/HelpWidget.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.setClassLoader(getClass().getClassLoader());
            loader.load();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        popup.getContent().add(textArea);

        textArea.prefHeightProperty().bind(textAreaHeight);
        textArea.prefWidthProperty().bind(textAreaWidth);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        toggleButton.setOnMouseClicked(event -> clickHelp());
    }


    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        textArea.textProperty().setValue(text);
    }

    public StringProperty textProperty() {
        return textArea.textProperty();
    }

    public DoubleProperty textAreaWidthProperty() {
        return textAreaWidth;
    }

    public double getTextAreaWidth() {
        return textAreaWidth.getValue();
    }

    public void setTextAreaWidth(double value) {
        textAreaWidth.setValue(value);
    }

    public DoubleProperty textAreaHeightProperty() {
        return textAreaHeight;
    }

    public double getTextAreaHeight() {
        return textAreaHeight.getValue();
    }

    public void setTextAreaHeight(double value) {
        textAreaHeight.setValue(value);
    }

    private void clickHelp() {
        currentVisible = !currentVisible;
        textArea.setVisible(currentVisible);
        Bounds boundsInScreen = toggleButton.localToScreen(toggleButton.getBoundsInLocal());
        popup.setX(boundsInScreen.getMinX() + 20);
        popup.setY(boundsInScreen.getMinY() + 20);
        popup.show(toggleButton.getScene().getWindow());
    }
}
