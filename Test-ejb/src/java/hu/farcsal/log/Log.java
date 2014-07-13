package hu.farcsal.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zoli
 */
public class Log {

    private static enum Type {
        
        OFF(Level.OFF),
        SEVERE(Level.SEVERE),
        WARNING(Level.WARNING),
        INFO(Level.INFO),
        FINE(Level.FINE),
        ALL(Level.ALL);
        
        private final Level LEVEL;
        
        private Type(Level level) {
            this.LEVEL = level;
        }
        
        private Level getLevel() {
            return LEVEL;
        }
    }
    
    private final Logger LOGGER;
    
    private Log(String name) {
        LOGGER = Logger.getLogger(name);
    }
    
    private Log(Class clazz) {
        this(clazz.getName());
    }
    
    public static Log getLogger(Class clazz) {
        return new Log(clazz);
    }
    
    public static Log getLogger(String name) {
        return new Log(name);
    }
    
    private void log(Type type, String text, Object... args) {
        LOGGER.log(type.getLevel(), String.format(text, args));
    }
    
    private void log(Type type, Throwable t, String text, Object... args) {
        LOGGER.log(type.getLevel(), String.format(text, args), t);
    }
    
    public void i(String text, Object... args) {
        log(Type.INFO, text, args);
    }
    
    public void w(String text, Object... args) {
        log(Type.WARNING, text, args);
    }
    
    public void e(String text, Object... args) {
        log(Type.SEVERE, text, args);
    }
    
    public void e(Throwable t, String text, Object... args) {
        log(Type.SEVERE, t, text, args);
    }
    
}
