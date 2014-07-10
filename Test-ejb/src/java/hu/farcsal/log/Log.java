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
    
    private void log(Type type, String text) {
        LOGGER.log(type.getLevel(), text);
    }
    
    private void log(Type type, String text, Throwable t) {
        LOGGER.log(type.getLevel(), text, t);
    }
    
    public void i(String text) {
        log(Type.INFO, text);
    }
    
    public void w(String text) {
        log(Type.WARNING, text);
    }
    
    public void e(String text) {
        log(Type.SEVERE, text);
    }
    
    public void e(String text, Throwable t) {
        log(Type.SEVERE, text, t);
    }
    
}
