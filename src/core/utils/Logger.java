/*
copied from: https://github.com/zserge/log
TODO: proper handle of licensing notice

 */

package core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

public final class Logger {

    public final static int DEBUG = 1;
    public final static int INFO = 2;
    public final static int WARNING = 3;
    public final static int ERROR = 4;

    private Logger() {}

    public interface Printer {
        void print(int level, String tag, String msg);
    }

    private static class SystemOutPrinter implements Printer {
        private final static String[] LEVELS = new String[]{"NONE", "[DEBUG]  ", "[INFO]   ", "[WARNING]", "[ERROR]  "};
        public void print(int level, String tag, String msg) {
            int logMethodsBefore = 5;
            int lineNumber = Thread.currentThread().getStackTrace()[logMethodsBefore].getLineNumber();
            //System.out.println("" + LEVELS[level] + "  " + tag + "(" + lineNumber + "): \t" + msg);

        }
    }


    private final static SystemOutPrinter SYSTEM = new SystemOutPrinter();

    private final static Map<String, String> mTags = new HashMap<>();

    private static String[] mUseTags = new String[]{"tag", "TAG"};
    private static boolean mUseFormat = false;
    private static int mMinLevel = DEBUG;

    private static Set<Printer> mPrinters = new HashSet<>();

    static {
        usePrinter(SYSTEM, true);
    }


    public static synchronized Logger level(int minLevel) {
        mMinLevel = minLevel;
        return null;
    }

    public static synchronized Logger useFormat(boolean yes) {
        mUseFormat = yes;
        return null;
    }

    public static synchronized Logger usePrinter(Printer p, boolean on) {
        if (on) {
            mPrinters.add(p);
        } else {
            mPrinters.remove(p);
        }
        return null;
    }

    public static synchronized void debug(Object msg, Object... args) {
        log(DEBUG, mUseFormat, msg, args);
    }
    public static synchronized void info(Object msg, Object... args) {
        log(INFO, mUseFormat, msg, args);
    }
    public static synchronized void warning(Object msg, Object... args) {
        log(WARNING, mUseFormat, msg, args);
    }
    public static synchronized void error(Object msg, Object... args) {
        log(ERROR, mUseFormat, msg, args);
    }

    private static void log(int level, boolean fmt, Object msg, Object... args) {
        if (level < mMinLevel) {
            return;
        }
        String tag = tag();
        if (mUseTags.length > 0 && tag.equals(msg)) {
            if (args.length > 1) {
                print(level, tag, format(fmt, args[0], Arrays.copyOfRange(args, 1, args.length)));
            } else {
                print(level, tag, format(fmt, (args.length > 0 ? args[0] : "")));
            }
        } else {
            print(level, tag, format(fmt, msg, args));
        }
    }

    private static String format(boolean fmt, Object msg, Object... args) {
        Throwable t = null;
        if (args == null) {
            // Null array is not supposed to be passed into this method, so it must
            // be a single null argument
            args = new Object[]{null};
        }
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            t = (Throwable) args[args.length - 1];
            args = Arrays.copyOfRange(args, 0, args.length - 1);
        }
        if (fmt && msg instanceof String) {
            String head = (String) msg;
            if (head.indexOf('%') != -1) {
                return String.format(head, args);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(msg == null ? "null" : msg.toString());
        for (Object arg : args) {
            sb.append("\t");
            sb.append(arg == null ? "null" : arg.toString());
        }
        if (t != null) {
            sb.append("\n");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            sb.append(sw.toString());
        }
        return sb.toString();
    }

    private final static int MAX_LOG_LINE_LENGTH = 4000;

    private static void print(int level, String tag, String msg) {
        for (String line : msg.split("\\n")) {
            do {
                int splitPos = Math.min(MAX_LOG_LINE_LENGTH, line.length());
                for (int i = splitPos-1; line.length() > MAX_LOG_LINE_LENGTH && i >= 0; i--) {
                    if (" \t,.;:?!{}()[]/\\".indexOf(line.charAt(i)) != -1) {
                        splitPos = i;
                        break;
                    }
                }
                splitPos = Math.min(splitPos + 1, line.length());
                String part = line.substring(0, splitPos);
                line = line.substring(splitPos);

                for (Printer p : mPrinters) {
                    p.print(level, tag, part);
                }
            } while (line.length() > 0);
        }
    }

    private final static Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");
    private final static int STACK_DEPTH = 4;
    private static String tag() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length < STACK_DEPTH) {
            throw new IllegalStateException
                    ("Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        String className = stackTrace[STACK_DEPTH-1].getClassName();
        String tag = mTags.get(className);
        if (tag != null) {
            return tag;
        }

        try {
            Class<?> c = Class.forName(className);
            for (String f : mUseTags) {
                try {
                    Field field = c.getDeclaredField(f);
                    if (field != null) {
                        field.setAccessible(true);
                        Object value = field.get(null);
                        if (value instanceof String) {
                            mTags.put(className, (String) value);
                            return (String) value;
                        }
                    }
                } catch (NoSuchFieldException|IllegalAccessException|
                        IllegalStateException|NullPointerException e) {
                    //Ignore
                }
            }
        } catch (ClassNotFoundException e) { /* Ignore */ }

        // Check class field useTag, if exists - return it, otherwise - return the generated tag
        Matcher m = ANONYMOUS_CLASS.matcher(className);
        if (m.find()) {
            className = m.replaceAll("");
        }
        return className.substring(className.lastIndexOf('.') + 1);
    }
}

