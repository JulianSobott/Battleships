package core.utils.logging;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class CoreFunctions {

    private static final String format = "[%1$s] [%2$s] %3$s %n \t\t\t\t[%4$tF %4$tT] (%5$s.%6$s Line: %7$s, Thread: " +
            "%8$s) %n";
    private static final String formatMinimal = "[%1$-7s] [%2$-7s] %3$s %n";

    private static final int STACK_TILL_FUNCTION = 11;

    public static final FileHandler fileHandlerAll;

    static {
        File logFolder = new File("logs");
        logFolder.mkdir();
        FileHandler fileHandlerAll1;
        Formatter f = new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord lr) {
                return CoreFunctions.formatMinimal(lr);
            }
        };
        try {
            fileHandlerAll1 = new FileHandler("logs/protocol.%u.log");
            fileHandlerAll1.setFormatter(f);

        } catch (IOException e) {
            fileHandlerAll1 = null;
            e.printStackTrace();
        }
        fileHandlerAll = fileHandlerAll1;
    }

    public static Level level = CustomLevel.DEBUG;

    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[STACK_TILL_FUNCTION].getLineNumber();
    }

    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[STACK_TILL_FUNCTION].getMethodName();
    }

    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[STACK_TILL_FUNCTION].getClassName();
    }

    public static String getThreadName() {
        return Thread.currentThread().getName();
    }

    public static String format(LogRecord lr){
        return String.format(format,
                lr.getLoggerName(),
                lr.getLevel(),
                lr.getMessage(),
                new Date(lr.getMillis()),
                CoreFunctions.getClassName(),
                CoreFunctions.getMethodName(),
                CoreFunctions.getLineNumber(),
                CoreFunctions.getThreadName()
        );
    }

    public static String formatMinimal(LogRecord lr) {
        return String.format(formatMinimal,
                lr.getLoggerName(),
                lr.getLevel(),
                lr.getMessage(),
                new Date(lr.getMillis())
        );
    }
}
