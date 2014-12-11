package com.eightpsman.funnyds.util;

import java.util.logging.*;

/**
 * FunnyDS
 * Created by 8psman on 11/26/2014.
 * Email: 8psman@gmail.com
 */
public class AppUtils {

    public static class IDUtil{
        public IDUtil(){
            NOW_ID = 0;
        }
        private int NOW_ID;
        public void reset(){
            NOW_ID  = 0;
        }
        public int nextID(){
            NOW_ID ++;
            return NOW_ID;
        }
    }

    public static IDUtil newIDUtil(){
        return new IDUtil();
    }

    /** logger */
    public static final String LOGGER_NAME = "FunnyDSGlobal";
    public static final Logger LOGGER = Logger.getLogger(LOGGER_NAME);

    static {
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
        /** custom console handler formatter */
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getLevel() + " : " + record.getMessage() + "\n";
            }
        });
        LOGGER.addHandler(consoleHandler);
    }

    public static float getActorIndependenceSize(int size){
        return (size + 3) / 10f;
    }

    public static float getActorIndependenceVelocity(int veloc){
        return (veloc) / 10f;
    }
}
