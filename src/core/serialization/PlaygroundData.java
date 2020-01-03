package core.serialization;

import core.Playground;

public class PlaygroundData {

    private int size;
    private Playground.Field[][] fields;

    public PlaygroundData(int size, Playground.Field[][] fields) {
        this.fields = fields;
        this.size = size;
    }

    public static PlaygroundData fromPlayground(Playground playground) {
        return new PlaygroundData(playground.getSize(), playground.getFields());
    }

    public int getSize() {
        return size;
    }

    public Playground.Field[][] getFields() {
        return fields;
    }
}
