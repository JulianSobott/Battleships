package core.utils.logging;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.*;

public class LoggerProfile {
    private static LoggerProfile instance = new LoggerProfile();
    private Logger logger;

    private HashMap<String, Long> times = new HashMap<>();

    private LoggerProfile() {
        logger = Logger.getLogger("Profile");
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
            fileHandler = new FileHandler("logs/profile.%u.log");
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


    private static void info(String msg) {
        instance._log(CustomLevel.INFO, msg);
    }

    private void _log(Level level, String msg) {
        logger.log(level, msg);
    }

    public static void start(String name) {instance.times.put(name, System.currentTimeMillis());}

    public static void stop(String name) {
        long time = System.currentTimeMillis() - instance.times.get(name);
        info(name + ": " + time + "ms");
    }
}
