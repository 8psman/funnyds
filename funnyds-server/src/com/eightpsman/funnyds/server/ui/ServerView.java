package com.eightpsman.funnyds.server.ui;

import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.rmi.RMIConstants;
import com.eightpsman.funnyds.server.ServerLogger;
import com.eightpsman.funnyds.server.ServerManager;
import com.eightpsman.funnyds.server.worker.StartServerWorker;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * FunnyDS
 * Created by 8psman on 11/19/2014.
 * Email: 8psman@gmail.com
 */
public class ServerView extends JFrame{

    LogView logView;
    ModeView presView, exhiView, gameView;

    public ServerView(){
        super("FunnyDS Server");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(styleTab("Exhibition")      , null, exhiView = new ModeView(ServerManager.getExhiInstance()), "Exhibition");
        tabbedPane.addTab(styleTab("Presentation")    , null, presView = new ModeView(ServerManager.getPresInstance()), "Presentation");
        tabbedPane.addTab(styleTab("Game")            , null, gameView = new ModeView(ServerManager.getGameInstance()), "Game");
        tabbedPane.addTab(styleTab("Log")             , null, logView = new LogView(), "Server log");

        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.setBackground(Color.CYAN);
        tabbedPane.setFocusable(false);
        tabbedPane.setBackgroundAt(0, Color.RED);

        add(new JLabel(
                "[RMI NAME: " + RMIConstants.RMI_NAME + "]     " +
                        "[RMI PORT: " + RMIConstants.RMI_PORT + "]     " +
                        "[SOCKET PORT: " + Constants.SERVER_SOCKET_PORT + "]"
        ), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ServerLogger.LOGGER.log(Level.INFO, "Shutting down server...");
                if (exhiView != null){
                    exhiView.destroy();
                    presView.destroy();
                    gameView.destroy();
                    ServerManager.shutdownAllInstance();
                }
                ServerLogger.LOGGER.log(Level.INFO, "Server is shutdown!");
            }
        });
        setVisible(true);
    }

    private String styleTab(String title){
        return String.format("<html><p align='center' style='padding:1px 0 1px 0; width: 80px; background:cyan;'>%s</p></html>", title);
    }
}
