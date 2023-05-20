package tsp.headdb.core.util;

import org.bukkit.Bukkit;
import tsp.nexuslib.util.StringUtils;

@SuppressWarnings("unused")
public class HeadDBLogger  {

    private final boolean debug;

    public HeadDBLogger(boolean debug) {
        this.debug = debug;
    }
    public void info(String message) {
        this.log(LogLevel.INFO, message);
    }

    public void warning(String message) {
        this.log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        this.log(LogLevel.ERROR, message);
    }

    public void debug(String message) {
        this.log(LogLevel.DEBUG, message);
    }

    public void trace(String message) {
        this.log(LogLevel.TRACE, message);
    }

    public void log(LogLevel level, String message) {
        if ((level == LogLevel.DEBUG || level == LogLevel.TRACE) && !debug) {
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.colorize("&cHeadDB &8>> " + level.getColor() + "[" + level.name() + "]: " + message));
    }

    public boolean isDebug() {
        return this.debug;
    }

    public enum LogLevel {
        INFO,
        WARNING,
        ERROR,
        DEBUG,
        TRACE;

        public String getColor() {
            return switch (this) {
                case INFO -> "\u001b[32m";
                case WARNING -> "\u001b[33m";
                case ERROR -> "\u001b[31m";
                case DEBUG -> "\u001b[36m";
                case TRACE -> "\u001b[35m";
            };
        }
    }

}
