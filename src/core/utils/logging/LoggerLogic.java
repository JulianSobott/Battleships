package core.utils.logging;

import java.util.logging.*;

public class LoggerLogic
{
    // TODO: make possible to set level

    private static LoggerLogic instance = new LoggerLogic();
    private Logger logger;


    private LoggerLogic() {
        logger = Logger.getLogger("Logic");
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
        logger.log(level, msg);
    }
}
