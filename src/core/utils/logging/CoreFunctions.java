package core.utils.logging;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class CoreFunctions {

    private static final String format = "[%1$s] [%2$s] %3$s %n \t\t\t\t[%4$tF %4$tT] (%5$s.%6$s Line: %7$s, Thread: " +
            "%8$s) %n";
    private static final String formatMinimal = "[%1$s] [%2$s] %3$s %n\t[%4$tF %4$tT] %n";

    private static final int STACK_TILL_FUNCTION = 11;

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
