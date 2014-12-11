package com.eightpsman.funnyds.server.ui;

import com.eightpsman.funnyds.server.ServerLogger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * FunnyDS
 * Created by 8psman on 11/21/2014.
 * Email: 8psman@gmail.com
 */
public class LogView extends JPanel{

    DefaultListModel<String> listModel;

    public LogView(){
        super();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));

        JList<String> jList = new JList<String>(listModel = new DefaultListModel<String>());
        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setPreferredSize(new Dimension(200, 0));
        add(jScrollPane);

        logHandler.setLevel(Level.INFO);
        ServerLogger.LOGGER.addHandler(logHandler);
    }

    Handler logHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            if (record.getLevel() == Level.INFO){
                listModel.addElement(record.getLevel() + " : " + record.getMessage());
            }
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };
}
