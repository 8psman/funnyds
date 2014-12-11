package com.eightpsman.funnyds.java.server;

import java.util.logging.*;

/**
 * FunnyDS
 * Created by 8psman on 11/21/2014.
 * Email: 8psman@gmail.com
 */
public class ServerLogger {

    public static final String LOGGER_NAME = "FunnyDS";
    public static final Logger LOGGER = Logger.getLogger(LOGGER_NAME);

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getLevel() + " : " + record.getMessage() + "\n";
            }
        });
        LOGGER.addHandler(consoleHandler);
    }
}
