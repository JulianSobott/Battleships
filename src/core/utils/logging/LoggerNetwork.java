package core.utils.logging;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class LoggerNetwork {

    private static LoggerNetwork instance = new LoggerNetwork();
    private Logger logger;


    private LoggerNetwork() {
        logger = Logger.getLogger("Network");
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
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler("logs/networkProtocol.%u.log");
            Formatter netF = new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return CoreFunctions.formatMinimal(lr);
                }
            };
            fileHandler.setFormatter(netF);
            fileHandler.setLevel(CustomLevel.DEBUG);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
