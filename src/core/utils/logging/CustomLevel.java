package core.utils.logging;

import java.util.logging.Level;

public class CustomLevel extends Level {

    public static final Level ERROR = new CustomLevel("ERROR", Level.SEVERE.intValue());
    public static final Level WARNING = new CustomLevel("WARNING", Level.WARNING.intValue());
    public static final Level INFO = new CustomLevel("INFO", Level.INFO.intValue());
    public static final Level DEBUG = new CustomLevel("DEBUG", Level.FINE.intValue());


    public CustomLevel(String name, int value) {
        super(name, value);
    }

}
