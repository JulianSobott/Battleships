package core.utils.logging;

import java.util.logging.*;

public class LoggerState {
    // TODO: make possible to set level

    private static LoggerState instance = new LoggerState();
    private Logger logger;


    private LoggerState() {
        logger = Logger.getLogger("STATE");
        Formatter f = new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord lr) {
                return CoreFunctions.format(lr);
            }
        };
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(f);
        handler.setLevel(CoreFunctions.level);
        logger.setLevel(CoreFunctions.level);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.addHandler(CoreFunctions.fileHandlerAll);
    }

    public static void debug(String msg) {
        instance._log(CustomLevel.DEBUG, msg);
    }

    public static void info(String msg) {
        instance._log(CustomLevel.INFO, msg);
    }

    public static void warning(String msg) {
        instance._log(CustomLevel.WARNING, msg);
    }

    public static void error(String msg) {
        instance._log(CustomLevel.ERROR, msg);
    }

    private void _log(Level level, String msg) {
        logger.log(level, "======= " + msg.toUpperCase() + " =======");
    }

}
