package gui.UiClasses;

import javafx.scene.layout.Pane;

public class PaneExtends extends Pane {

    private FieldType fieldType;


    public PaneExtends(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public enum FieldType{
        SHIP, WATER, FOG;
    }

}
