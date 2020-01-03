package core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
abstract class PlaygroundElement {

    public PlaygroundElement() {
    }

    public void gotHit() {

    }
}
